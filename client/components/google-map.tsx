import React from "react";

interface MapProps {
  street: string;
  city: string;
  state: string;
  zip: string;
  width?: string | number;
  height?: string | number;
}

const Map: React.FC<MapProps> = ({
  street,
  city,
  state,
  zip,
  width = "100%",
  height = 400
}) => {
  const address = `${street}, ${city}, ${state} ${zip}`;
  const query = encodeURIComponent(address);
  const mapSrc = `https://maps.google.com/maps?q=${query}&output=embed`;

  return (
    <div className="rounded-lg overflow-hidden shadow-md mt-5 mb-5">
      <iframe
        title={address}
        src={mapSrc}
        width={width}
        height={height}
        style={{ border: 0 }}
        loading="lazy"
        allowFullScreen
      />
    </div>
  );
};

export { Map };
