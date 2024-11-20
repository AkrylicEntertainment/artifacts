import Link from 'next/link'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Github, Package, Menu, Search } from 'lucide-react'

export default function HomePage() {
  const popularArtifacts = [
    { id: 1, name: 'spring-boot', author: 'spring-projects', downloads: '1M+' },
    { id: 2, name: 'gson', author: 'google', downloads: '500K+' },
    { id: 3, name: 'retrofit', author: 'square', downloads: '250K+' },
    { id: 4, name: 'lombok', author: 'projectlombok', downloads: '200K+' },
  ]

  return (
    <div className="flex flex-col min-h-screen bg-background text-foreground">
      <header className="border-b border-border sticky top-0 z-10 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <Link href="/" className="text-xl md:text-2xl font-bold flex items-center text-primary">
            <Package className="mr-2 h-6 w-6 md:h-8 md:w-8" />
            <span className="hidden sm:inline">ByteStore</span>
          </Link>
          <nav className="hidden md:block">
            <ul className="flex space-x-4">
              <li><Link href="/login" className="text-foreground hover:text-primary transition-colors">Login</Link></li>
              <li><Link href="/account" className="text-foreground hover:text-primary transition-colors">Account</Link></li>
            </ul>
          </nav>
          <Button variant="ghost" size="icon" className="md:hidden text-foreground hover:text-primary">
            <Menu className="h-6 w-6" />
            <span className="sr-only">Menu</span>
          </Button>
        </div>
      </header>

      <main className="flex-grow container mx-auto px-4 py-8">
        <section className="text-center mb-12">
          <h1 className="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-primary">Welcome to ByteStore</h1>
          <p className="text-lg md:text-xl mb-8 text-muted-foreground max-w-2xl mx-auto">Build and serve Java artifacts from GitHub repositories with ease</p>
          <div className="flex flex-col sm:flex-row max-w-md mx-auto">
            <div className="relative flex-grow mb-2 sm:mb-0 sm:mr-2">
              <Input type="text" placeholder="Search for a repository..." className="pl-10 bg-secondary text-secondary-foreground" />
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
            </div>
            <Button type="submit" className="w-full sm:w-auto bg-primary text-primary-foreground hover:bg-primary/90">Search</Button>
          </div>
        </section>

        <section>
          <h2 className="text-2xl font-semibold mb-4 text-primary">Popular Artifacts</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {popularArtifacts.map((artifact) => (
              <Card key={artifact.id} className="bg-card border-border hover:border-primary transition-colors">
                <CardHeader>
                  <CardTitle className="text-lg text-primary">{artifact.name}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground mb-2">by {artifact.author}</p>
                  <p className="text-sm text-foreground">{artifact.downloads} downloads</p>
                  <Button variant="outline" className="w-full mt-4 border-primary text-primary hover:bg-primary hover:text-primary-foreground">
                    <Github className="mr-2 h-4 w-4" />
                    View on GitHub
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </section>
      </main>

      <footer className="border-t border-border mt-12">
        <div className="container mx-auto px-4 py-6 text-center text-sm text-muted-foreground">
          © {new Date().getFullYear()} ByteStore. All rights reserved.
        </div>
      </footer>
    </div>
  )
}