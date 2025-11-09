import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigation-menu"
import Image from 'next/image';

export function Navigation() {
  return (
    <nav className="flex text-2xl text-white p-5 justify-between w-screen bg-blue-900 shadow-md z-50">
      <div>
        <Image src="/Lotlytics.png" alt="Lotlytics" width="80" height="80"/>
      </div>
      <NavigationMenu>
        <NavigationMenuList className="min-w-md flex justify-end gap-4">
          <NavigationMenuItem>
              <p>About</p>
          </NavigationMenuItem>
          <NavigationMenuItem>
              <p>My Lots</p>
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </nav>
  )
}
