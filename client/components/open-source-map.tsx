'use client';

import { useState, useEffect, useRef } from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { MapPin } from 'lucide-react';
import { Address, AddressObject, createAddress } from '@/types/address';
import 'leaflet/dist/leaflet.css';

interface MapProps {
  onAddress?: (address: Address) => void
}

export function MapComponent({ onAddress }: MapProps) {

  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const mapRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<any>(null);
  const markerRef = useRef<any>(null);
  const LeafletRef = useRef<any>(null);

  const handleNewAddress = (addressObject: AddressObject) => {
    console.log(addressObject)
    if (!onAddress || !addressObject) {
      return;
    }
    let street = `${addressObject.house_number != null ? addressObject.house_number : ""}`;
    if (street === "") {
      street = `${addressObject.road != null ? addressObject.road : ""}`;
    } else {
      street += ` ${addressObject.road != null ? addressObject.road : ""}`;
    }

    let city = addressObject.city;
    let state = addressObject.state;
    let zip = addressObject.postcode;
    onAddress(createAddress(
      street,
      city,
      state,
      zip
    ));
  }

  useEffect(() => {
    // Use a dynamic import to ensure leaflet only runs on the client
    import('leaflet').then((L) => {
      LeafletRef.current = L.default || L;
      
      // Fix for default marker icons
      delete (LeafletRef.current.Icon.Default.prototype as any)._getIconUrl;
      LeafletRef.current.Icon.Default.mergeOptions({
        iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
        iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
      });

      if (mapRef.current && !mapInstanceRef.current) {
        // Initialize map
        mapInstanceRef.current = LeafletRef.current.map(mapRef.current).setView([51.505, -0.09], 13);
        
        // Add OpenStreetMap tiles
        LeafletRef.current.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
          maxZoom: 19
        }).addTo(mapInstanceRef.current);

        // Add initial marker
        markerRef.current = LeafletRef.current.marker([51.505, -0.09]).addTo(mapInstanceRef.current);
        
        setIsLoading(false);
      }
    });

    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;

    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(searchQuery)}&limit=5&addressdetails=1`
      );
      const data = await response.json();
      setSearchResults(data);

      if (data.length > 0 && mapInstanceRef.current && LeafletRef.current) {
        const { lat, lon, display_name, address } = data[0];
        const newLat = parseFloat(lat);
        const newLng = parseFloat(lon);
        
        handleNewAddress(address || null);
                
        mapInstanceRef.current.setView([newLat, newLng], 13);
        
        if (markerRef.current) {
          markerRef.current.setLatLng([newLat, newLng]);
        } else {
          markerRef.current = LeafletRef.current.marker([newLat, newLng]).addTo(mapInstanceRef.current);
        }
      }
    } catch (error) {
      console.error('Search error:', error);
    }
  };

  const handleResultClick = (result: any) => {
    const lat = parseFloat(result.lat);
    const lng = parseFloat(result.lon);
    
    handleNewAddress(result.address || null);
        
    if (mapInstanceRef.current && LeafletRef.current) {
      mapInstanceRef.current.setView([lat, lng], 13);
      
      if (markerRef.current) {
        markerRef.current.setLatLng([lat, lng]);
      } else {
        markerRef.current = LeafletRef.current.marker([lat, lng]).addTo(mapInstanceRef.current);
      }
    }
    
    setSearchResults([]);
    setSearchQuery(result.display_name);
  };

  return (
    <div className="w-full flex flex-col">
      <p className="sm:text-md md:text-sm">Lot Location</p>
      <p className="sm:text-md md:text-sm"><span className="font-bold">Note: </span>Parking Lots can only operate within the United States.</p>
      <Card className="mt-4 mb-4 p-4">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Input
              type="text"
              placeholder="Search for your lot..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              disabled={isLoading}
            />
            {searchResults.length > 0 && (
              <div className="absolute top-full left-0 right-0 mt-1 bg-white border rounded-md shadow-lg max-h-60 overflow-y-auto" style={{ zIndex: 1001 }}>
                {searchResults.map((result: any, index: number) => (
                  <div
                    key={index}
                    onClick={() => handleResultClick(result)}
                    className="p-3 hover:bg-gray-100 cursor-pointer border-b last:border-b-0 flex items-start gap-2"
                  >
                    <MapPin className="w-4 h-4 mt-1 shrink-0 text-gray-500" />
                    <span className="text-sm">{result.display_name}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
          <Button type="button" className="bg-blue-900 hover:bg-blue-400" onClick={handleSearch} disabled={isLoading}>
            {isLoading ? 'Loading...' : 'Search'}
          </Button>
        </div>
      </Card>
      

      <div className="flex-1 mb-4 ml-5 mr-5 lg:ml-0 lg:mr-0">
        <div ref={mapRef} className="w-full h-96 rounded-lg shadow-lg">
          {isLoading && (
            <div className="w-full h-full flex items-center justify-center bg-gray-100 rounded-lg">
              <p className="text-gray-500">Loading map...</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}