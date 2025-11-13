import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigation-menu"
import Image from 'next/image';

export function Navigation() {
  return (
    <nav className="flex sticky top-0 text-2xl text-white p-5 justify-between w-screen bg-blue-900 shadow-md z-1002">
      <div>
        <a href="#">
          <Image src="/Lotlytics.avif" alt="Lotlytics" width="60" height="60"/>
        </a>
      </div>
      <NavigationMenu>
        <NavigationMenuList className="md:min-w-md flex justify-end gap-4">
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
