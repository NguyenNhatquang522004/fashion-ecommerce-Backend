"use client";

import { useEffect } from "react";

export default function Home() {
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("is-visible");
          }
        });
      },
      { threshold: 0.1, rootMargin: "0px 0px -50px 0px" }
    );

    const elements = document.querySelectorAll(".reveal-on-scroll");
    elements.forEach((el) => observer.observe(el));

    return () => observer.disconnect();
  }, []);

  return (
    <div className="bg-white min-h-screen text-[#111] font-sans overflow-x-hidden selection:bg-black selection:text-white w-full">
      {/* Navigation */}
      <nav className="fixed w-full z-50 flex justify-between items-center px-6 md:px-8 py-6 mix-blend-difference text-white">
        <div className="text-sm font-light tracking-[0.2em] uppercase">Élégance</div>
        <div className="hidden md:flex gap-8 text-xs font-light tracking-[0.15em] uppercase">
          <a href="#collections" className="hover:opacity-70 transition-opacity">Collections</a>
          <a href="#editorial" className="hover:opacity-70 transition-opacity">Editorial</a>
          <a href="#philosophy" className="hover:opacity-70 transition-opacity">Philosophy</a>
          <a href="#" className="hover:opacity-70 transition-opacity">Cart (0)</a>
        </div>
        <button className="md:hidden text-xs font-light tracking-widest uppercase hover:opacity-70 transition-opacity">Menu</button>
      </nav>

      {/* Hero Section */}
      <section className="relative h-[100dvh] w-full flex items-center justify-center overflow-hidden bg-[#111]">
        <div className="absolute inset-0 w-full h-full bg-gradient-to-t from-black/40 via-black/10 to-black/20 z-10" />
        <img
          src="https://images.unsplash.com/photo-1490481651871-ab68de25d43d?q=80&w=2070&auto=format&fit=crop"
          alt="Fashion Hero"
          className="absolute inset-0 w-full h-full object-cover animate-image-zoom origin-center opacity-90"
        />
        
        <div className="z-20 text-center flex flex-col items-center px-4 w-full">
          <span className="text-white text-xs md:text-sm font-light tracking-[0.4em] uppercase mb-8 animate-fade-up">
            Fall / Winter 2026
          </span>
          <h1 className="text-white text-5xl sm:text-7xl md:text-8xl lg:text-[10rem] font-extralight tracking-tighter mb-8 animate-fade-up-delay-1 drop-shadow-lg break-words w-full">
            ÉLÉGANCE
          </h1>
          <p className="text-white/90 font-light max-w-xl mb-14 text-sm md:text-base animate-fade-up-delay-2 tracking-wide leading-relaxed drop-shadow-md">
            Discover the essence of modern luxury. A collection defined by pure lines, 
            exquisite fabrics, and uncompromising craftsmanship.
          </p>
          <a
            href="#collections"
            className="group relative px-8 py-4 sm:px-10 sm:py-5 bg-transparent border border-white/50 text-white text-xs tracking-[0.2em] uppercase overflow-hidden transition-colors animate-fade-up-delay-2 hover:border-white"
          >
            <span className="relative z-10 group-hover:text-black transition-colors duration-500">
              Discover the Collection
            </span>
            <div className="absolute inset-0 bg-white translate-y-full group-hover:translate-y-0 transition-transform duration-500 ease-[cubic-bezier(0.19,1,0.22,1)]" />
          </a>
        </div>
      </section>

      {/* Philosophy Section */}
      <section id="philosophy" className="py-24 sm:py-32 px-4 md:px-8 bg-white flex flex-col items-center justify-center text-center">
        <div className="max-w-4xl mx-auto reveal-on-scroll">
          <span className="text-xs font-light tracking-[0.2em] uppercase text-gray-400 mb-8 block">Our Philosophy</span>
          <h2 className="text-3xl md:text-5xl lg:text-6xl font-light tracking-tight mb-8 sm:mb-10 text-gray-900 leading-tight">
            The Art of Subtlety
          </h2>
          <p className="text-gray-500 text-base sm:text-lg md:text-2xl font-light leading-relaxed mb-12 sm:mb-16 max-w-3xl mx-auto px-4">
            Every piece is imbued with a quiet confidence. We believe that true luxury 
            whispers rather than shouts. Our garments are crafted to be lived in, 
            loved, and passed down through generations.
          </p>
          <div className="w-px h-24 sm:h-32 bg-gray-200 mx-auto reveal-on-scroll scale-y-0 transform origin-top transition-transform duration-1000 delay-300"></div>
        </div>
      </section>

      {/* Curated Collection */}
      <section id="collections" className="py-16 sm:py-24 px-4 md:px-8 max-w-[1700px] mx-auto w-full">
        <div className="flex justify-center md:justify-between items-end mb-12 sm:mb-16 reveal-on-scroll">
          <h2 className="text-3xl md:text-4xl font-light tracking-tight text-center md:text-left">Curated Selection</h2>
          <a href="#" className="hidden md:block text-xs uppercase tracking-[0.15em] border-b border-gray-300 pb-1 hover:text-black hover:border-black transition-colors">
            View the Lookbook
          </a>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 sm:gap-8 md:gap-10">
          {[
            { img: "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?q=80&w=1000&auto=format&fit=crop", name: "Structured Trench", price: "$1,250" },
            { img: "https://images.unsplash.com/photo-1550614000-4b95d46621ab?q=80&w=1000&auto=format&fit=crop", name: "Leather Tote", price: "$890" },
            { img: "https://images.unsplash.com/photo-1584273143981-41c073dfe8f8?q=80&w=1000&auto=format&fit=crop", name: "Pleated Skirt", price: "$650" },
            { img: "https://images.unsplash.com/photo-1539109136881-3be0616acf4b?q=80&w=1000&auto=format&fit=crop", name: "Cashmere Knit", price: "$540" },
          ].map((item, idx) => (
            <div key={idx} className="group cursor-pointer reveal-on-scroll flex flex-col w-full" style={{ transitionDelay: `${idx * 150}ms` }}>
              <div className="relative aspect-[3/4] w-full overflow-hidden bg-gray-50 mb-4 sm:mb-6">
                <img
                  src={item.img}
                  alt={item.name}
                  className="w-full h-full object-cover mix-blend-multiply group-hover:scale-105 transition-transform duration-[2s] ease-[cubic-bezier(0.19,1,0.22,1)]"
                />
                <div className="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors duration-700"></div>
              </div>
              <div className="flex justify-between items-center px-1">
                <h3 className="text-sm font-light tracking-wide">{item.name}</h3>
                <span className="text-sm font-light text-gray-500">{item.price}</span>
              </div>
            </div>
          ))}
        </div>
        
        <div className="mt-12 flex justify-center md:hidden reveal-on-scroll">
          <a href="#" className="text-xs uppercase tracking-[0.15em] border-b border-gray-300 pb-1 hover:text-black hover:border-black transition-colors transform hover:scale-105">
            View the Lookbook
          </a>
        </div>
      </section>

      {/* Feature Split Section */}
      <section id="editorial" className="py-20 sm:py-32 px-4 md:px-8 max-w-[1700px] mx-auto w-full">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 sm:gap-20 items-center">
          <div className="relative aspect-[4/5] w-full overflow-hidden reveal-on-scroll shadow-2xl order-2 lg:order-1">
            <img 
              src="https://images.unsplash.com/photo-1483985988355-763728e1935b?q=80&w=1000&auto=format&fit=crop" 
              alt="Editorial" 
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-[2s]"
            />
          </div>
          <div className="flex flex-col justify-center reveal-on-scroll lg:pl-12 lg:pr-24 order-1 lg:order-2 text-center lg:text-left">
            <span className="text-xs font-light tracking-[0.2em] uppercase text-gray-400 mb-6 sm:mb-8 block">The Process</span>
            <h2 className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-light mb-8 sm:mb-10 leading-[1.2] lg:leading-[1.1] text-gray-900">
              Meticulous detail in every stitch.
            </h2>
            <p className="text-gray-500 font-light leading-relaxed mb-10 sm:mb-12 text-base sm:text-lg px-4 lg:px-0">
              Behind every garment is a story of countless hours, unyielding dedication, and a pursuit 
              of perfection. We source only the finest materials from heritage mills across the globe,
              ensuring that comfort and durability walk hand-in-hand with style.
            </p>
            <div className="flex justify-center lg:justify-start">
              <a href="#" className="inline-flex items-center text-xs uppercase tracking-[0.15em] hover:text-gray-600 transition-colors group">
                Discover the Craft 
                <span className="ml-4 w-12 h-[1px] bg-black group-hover:bg-gray-400 transition-colors group-hover:w-16 duration-300"></span>
              </a>
            </div>
          </div>
        </div>
      </section>

      {/* Full Width Banner */}
      <section className="w-full h-[50vh] sm:h-[60vh] relative flex items-center justify-center reveal-on-scroll">
        <img 
          src="https://images.unsplash.com/photo-1496747611176-843222e1e57c?q=80&w=2073&auto=format&fit=crop" 
          alt="Campaign" 
          className="absolute inset-0 w-full h-full object-cover brightness-[0.6]"
        />
        <div className="relative z-10 text-center px-4 w-full">
          <h2 className="text-white text-2xl sm:text-3xl md:text-5xl font-light tracking-wide mb-6 sm:mb-8 leading-snug">Ready to wear. Made to last.</h2>
          <a
            href="#"
            className="inline-block px-6 py-3 sm:px-8 sm:py-4 bg-white text-black text-[10px] sm:text-xs tracking-[0.2em] uppercase hover:bg-black hover:text-white transition-colors duration-500"
          >
            Shop Essentials
          </a>
        </div>
      </section>

      {/* Newsletter */}
      <section className="py-24 sm:py-40 bg-[#fafafa] flex justify-center text-center px-4 sm:px-8 reveal-on-scroll w-full">
        <div className="max-w-xl w-full">
          <h2 className="text-2xl sm:text-3xl font-light mb-4 text-gray-900">Join the Inner Circle</h2>
          <p className="text-gray-500 font-light mb-10 sm:mb-12 text-sm sm:text-base px-2">Subscribe to receive exclusive access to our newest collections, private events, and early sales.</p>
          <form className="flex flex-col sm:flex-row border-b border-gray-300 hover:border-black transition-colors duration-300 pb-3 gap-4 sm:gap-0" onSubmit={(e) => e.preventDefault()}>
            <input 
              type="email" 
              placeholder="Enter your email address" 
              className="flex-1 bg-transparent border-none outline-none font-light placeholder:text-gray-400 placeholder:font-light text-center sm:text-left"
              required
            />
            <button type="submit" className="text-xs uppercase tracking-[0.1em] text-gray-900 hover:text-gray-500 transition-colors sm:pl-4 mt-2 sm:mt-0 pb-2 sm:pb-0">
              Subscribe
            </button>
          </form>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-white pt-16 sm:pt-24 pb-8 sm:pb-12 px-6 md:px-8 lg:px-12 border-t border-gray-100 w-full">
        <div className="max-w-[1700px] mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-10 sm:gap-12 mb-16 sm:mb-24">
          <div className="col-span-1 sm:col-span-2 md:col-span-2 lg:col-span-2 text-center sm:text-left">
            <h2 className="text-2xl font-light tracking-tight mb-6 sm:mb-8">ÉLÉGANCE</h2>
            <p className="text-gray-500 font-light max-w-sm leading-relaxed mx-auto sm:mx-0 text-sm sm:text-base">
              Redefining modern luxury through minimalist design and unmatched quality. 
              Elevating the everyday.
            </p>
          </div>
          <div className="flex flex-col gap-3 sm:gap-4 text-center sm:text-left">
            <h4 className="text-xs uppercase tracking-[0.1em] font-medium mb-2 sm:mb-4 text-gray-900">Collections</h4>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Spring / Summer</a>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Fall / Winter</a>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Essentials</a>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Accessories</a>
          </div>
          <div className="flex flex-col gap-3 sm:gap-4 text-center sm:text-left">
            <h4 className="text-xs uppercase tracking-[0.1em] font-medium mb-2 sm:mb-4 text-gray-900">Company</h4>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">About Us</a>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Careers</a>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Stores</a>
          </div>
          <div className="flex flex-col gap-3 sm:gap-4 text-center sm:text-left">
            <h4 className="text-xs uppercase tracking-[0.1em] font-medium mb-2 sm:mb-4 text-gray-900">Support</h4>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Contact</a>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">Returns</a>
            <a href="#" className="text-gray-500 font-light text-sm hover:text-black transition-colors">FAQ</a>
          </div>
        </div>
        <div className="max-w-[1700px] mx-auto flex flex-col md:flex-row justify-between items-center text-[10px] md:text-xs text-gray-400 font-light tracking-wider border-t border-gray-100 pt-8 mt-8 text-center md:text-left">
          <p className="mb-4 md:mb-0">&copy; 2026 ÉLÉGANCE. ALL RIGHTS RESERVED.</p>
          <div className="flex flex-wrap justify-center md:justify-end gap-4 sm:gap-6">
            <a href="#" className="hover:text-black transition-colors">PRIVACY POLICY</a>
            <a href="#" className="hover:text-black transition-colors">TERMS OF SERVICE</a>
          </div>
        </div>
      </footer>
    </div>
  );
}
