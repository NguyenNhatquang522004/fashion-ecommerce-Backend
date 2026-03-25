"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Mail, Lock, Eye, EyeOff, Loader2, CheckCircle2, Home, ArrowRight, ShieldAlert } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";

// Real OAuth Providers for Google and Facebook
import { GoogleOAuthProvider, useGoogleLogin } from "@react-oauth/google";
import FacebookLogin from "react-facebook-login/dist/facebook-login-render-props";

const API_BASE_URL = "http://localhost:8080/api/v1/auth/login";

// Environment variables should be placed in .env.local
const GOOGLE_CLIENT_ID = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID || "YOUR_GOOGLE_CLIENT_ID";
const FACEBOOK_APP_ID = process.env.NEXT_PUBLIC_FACEBOOK_APP_ID || "YOUR_FACEBOOK_APP_ID";

function LoginForm() {
  const router = useRouter();
  
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [isLoading, setIsLoading] = useState(false);
  const [socialLoading, setSocialLoading] = useState<"google" | "facebook" | null>(null);
  const [errorText, setErrorText] = useState("");
  const [successText, setSuccessText] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrorText("");
    setSuccessText("");
  };

  // ============================================
  // 1. LOCAL LOGIN STRATEGY (Email / Password)
  // ============================================
  const handleLocalLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.email) return setErrorText("Email is required");
    if (!formData.password) return setErrorText("Password is required");

    setIsLoading(true);
    setErrorText("");
    try {
      const res = await fetch(API_BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          loginType: "local",
          email: formData.email,
          password: formData.password,
        }),
      });
      const data = await res.json();
      
      if (!res.ok) {
        throw new Error(data.message || "Invalid credentials. Please try again.");
      }
      
      setSuccessText("Authentication successful. Redirecting...");
      setTimeout(() => router.push("/"), 1500);
      
    } catch (err: any) {
      setErrorText(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  // ============================================
  // 2. REAL GOOGLE LOGIN OAUTH FLOW
  // Hook provided by @react-oauth/google
  // ============================================
  const loginGoogle = useGoogleLogin({
    onSuccess: async (tokenResponse) => {
      setSocialLoading("google");
      setErrorText("");
      try {
        // Send the real Google Access Token obtained directly from Google OAuth popup to our system
        const res = await fetch(API_BASE_URL, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            loginType: "google",
            providerToken: tokenResponse.access_token, 
          }),
        });
        const data = await res.json();
        
        if (!res.ok) {
          throw new Error(data.message || "Google Authentication failed or Token invalid.");
        }
        setSuccessText("Google sign in successful!");
        setTimeout(() => router.push("/"), 1500);
      } catch (err: any) {
        setErrorText(err.message);
      } finally {
        setSocialLoading(null);
      }
    },
    onError: (errorResponse) => {
      console.error("Google Login Error:", errorResponse);
      setErrorText("Google popup was closed or authentication failed.");
      setSocialLoading(null);
    },
  });

  // ============================================
  // 3. REAL FACEBOOK LOGIN OAUTH FLOW
  // Callback directly triggered by Facebook SDK via react-facebook-login
  // ============================================
  const handleFacebookResponse = async (response: any) => {
    // If user closes popup or fb auth fails, accessToken won't exist
    if (!response || !response.accessToken) {
      setErrorText("Facebook popup was closed or authentication failed.");
      setSocialLoading(null);
      return;
    }

    setSocialLoading("facebook");
    setErrorText("");
    
    try {
      // Send the real Facebook Graph Access Token directly to our backend system
      const res = await fetch(API_BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          loginType: "facebook",
          providerToken: response.accessToken, 
        }),
      });
      const data = await res.json();
      
      if (!res.ok) {
        throw new Error(data.message || "Facebook Authentication failed or Token invalid.");
      }
      setSuccessText("Facebook sign in successful!");
      setTimeout(() => router.push("/"), 1500);
    } catch (err: any) {
      setErrorText(err.message);
    } finally {
      setSocialLoading(null);
    }
  };


  const animations: any = {
    initial: { opacity: 0, y: 20, filter: "blur(4px)" },
    animate: { opacity: 1, y: 0, filter: "blur(0px)" },
    transition: { duration: 0.6, ease: "easeOut" }
  };

  return (
    <div className="min-h-screen bg-neutral-50 flex flex-col font-sans selection:bg-neutral-900 selection:text-white">
      {/* Navbar Minimalist */}
      <nav className="w-full py-6 px-8 flex items-center justify-between border-b border-neutral-200 bg-white/50 backdrop-blur-md sticky top-0 z-50">
        <Link href="/" className="flex items-center gap-2 group cursor-pointer transition-opacity hover:opacity-70">
          <div className="bg-neutral-900 text-white p-2 rounded-sm">
            <Home size={18} strokeWidth={2.5} />
          </div>
          <span className="font-semibold text-xl tracking-tight text-neutral-900 uppercase">Maison <span className="font-light">Élégante</span></span>
        </Link>
        <div className="text-sm tracking-wide text-neutral-500 font-medium">
          NEW CUSTOMER? <Link href="/register" className="text-neutral-900 underline underline-offset-4 decoration-neutral-300 hover:decoration-neutral-900 transition-colors duration-300">CREATE ACCOUNT</Link>
        </div>
      </nav>

      <main className="flex-1 flex flex-col items-center justify-center p-6 md:p-12 relative overflow-hidden">
        {/* Abstract Background Gradient Blob */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-neutral-200/40 rounded-full blur-[120px] -z-10 pointer-events-none opacity-50" />
        
        <motion.div 
          {...animations}
          className="w-full max-w-lg bg-white rounded-3xl shadow-[0_8px_40px_-12px_rgba(0,0,0,0.1)] border border-neutral-100 p-8 md:p-12 z-10"
        >
          <div className="text-center mb-10">
             <h1 className="text-3xl font-light text-neutral-900 tracking-tight mb-2">Welcome Back</h1>
             <p className="text-neutral-500 text-sm">Log in to exclusively manage your collections, orders, and premium profile.</p>
          </div>

          <AnimatePresence mode="popLayout">
            {errorText && (
              <motion.div 
                initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} 
                className="mb-6 p-4 rounded-xl bg-red-50 border border-red-100 text-red-600 text-sm font-medium flex items-start gap-3"
              >
                <ShieldAlert size={18} className="mt-0.5 flex-shrink-0 text-red-500" />
                <span>{errorText}</span>
              </motion.div>
            )}
            {successText && (
              <motion.div 
                initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} 
                className="mb-6 p-4 rounded-xl bg-emerald-50 border border-emerald-100 text-emerald-700 text-sm font-medium flex items-start gap-3"
              >
                <CheckCircle2 size={18} className="mt-0.5 flex-shrink-0 text-emerald-600" />
                <span>{successText}</span>
              </motion.div>
            )}
          </AnimatePresence>

          <form onSubmit={handleLocalLogin} className="space-y-5">
            <div className="group relative">
              <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-2 block">Email Address</label>
              <div className="relative flex items-center">
                 <Mail className="absolute left-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                 <input 
                   name="email" type="email" value={formData.email} onChange={handleChange} required
                   className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-4 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium hover:bg-neutral-50"
                   placeholder="youremail@example.com"
                 />
              </div>
            </div>

            <div className="group relative">
              <div className="flex justify-between items-center mb-2">
                 <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-0 block">Password</label>
                 <Link href="/forgot-password" className="text-xs font-semibold text-neutral-400 hover:text-neutral-900 transition-colors">Forgot?</Link>
              </div>
              <div className="relative flex items-center">
                 <Lock className="absolute left-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                 <input 
                   name="password" type={showPassword ? "text" : "password"} value={formData.password} onChange={handleChange} required
                   className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-12 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium hover:bg-neutral-50"
                   placeholder="Enter your password"
                 />
                 <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-4 text-neutral-400 hover:text-neutral-900 transition-colors">
                    {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                 </button>
              </div>
            </div>

            <button 
              disabled={isLoading || socialLoading !== null} type="submit" 
              className="w-full mt-4 bg-neutral-900 hover:bg-neutral-800 text-white rounded-xl py-4 font-semibold tracking-wide flex items-center justify-center gap-2 transition-all active:scale-[0.98] disabled:opacity-70 group"
            >
              {isLoading ? <Loader2 className="animate-spin" size={20} /> : (
                <>Sign In <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" /></>
              )}
            </button>
          </form>

          {/* Divider */}
          <div className="flex items-center gap-4 my-8">
            <div className="flex-1 h-[1px] bg-neutral-200"></div>
            <span className="text-xs text-neutral-400 uppercase tracking-widest font-semibold">Or Connect With</span>
            <div className="flex-1 h-[1px] bg-neutral-200"></div>
          </div>

          {/* Social Logins */}
          <div className="grid grid-cols-2 gap-4">
            
            {/* GOOGLE BUTTON: triggers the hook. */}
            <button 
              type="button"
              onClick={() => { setSocialLoading("google"); loginGoogle(); }} 
              disabled={socialLoading !== null || isLoading}
              className="w-full relative py-3.5 flex items-center justify-center gap-3 bg-white border border-neutral-200 hover:bg-neutral-50 rounded-xl font-medium text-neutral-700 transition-all active:scale-[0.98] disabled:opacity-70"
            >
              {socialLoading === "google" ? <Loader2 className="animate-spin" size={18} /> : (
                <svg viewBox="0 0 24 24" className="w-5 h-5">
                  <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                  <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                  <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                  <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                </svg>
              )}
              <span>Google</span>
            </button>

            {/* FACEBOOK BUTTON: Component wraps a render prop. */}
            {/* The 'autoLoad=false' prevents popup spam. onClick triggers the core SDK popup. */}
            <FacebookLogin
              appId={FACEBOOK_APP_ID}
              autoLoad={false}
              callback={handleFacebookResponse}
              render={(renderProps: any) => (
                <button 
                  type="button"
                  onClick={() => { setSocialLoading("facebook"); renderProps.onClick(); }} 
                  disabled={socialLoading !== null || isLoading}
                  className="w-full relative py-3.5 flex items-center justify-center gap-3 bg-[#1877F2] hover:bg-[#166FE5] text-white rounded-xl font-medium transition-all active:scale-[0.98] disabled:opacity-70"
                >
                  {socialLoading === "facebook" ? <Loader2 className="animate-spin text-white" size={18} /> : (
                    <svg viewBox="0 0 24 24" className="w-5 h-5" fill="currentColor">
                      <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.469h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.469h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                    </svg>
                  )}
                  <span>Facebook</span>
                </button>
              )}
            />

          </div>
          
        </motion.div>
        
        {/* Footer info minimalist */}
        <div className="mt-12 text-neutral-400 text-xs font-medium tracking-widest uppercase z-10">
          © {new Date().getFullYear()} Maison Élégante. All Rights Reserved.
        </div>
      </main>
    </div>
  );
}

// ---------------------------------------------------------
// EXPORT PAGE COMPONENT WRAPPED WITH GOOGLE OAUTH PROVIDER
// ---------------------------------------------------------
export default function LoginPage() {
  return (
    <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
      <LoginForm />
    </GoogleOAuthProvider>
  );
}
