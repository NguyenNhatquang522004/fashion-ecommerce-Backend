"use client";

import React, { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { 
  Mail, ShieldCheck, User, MapPin, Phone, Lock, 
  Eye, EyeOff, Loader2, CheckCircle2, ArrowRight, ArrowLeft, Home, BadgeCheck 
} from "lucide-react";
import Link from "next/link";

const API_BASE_URL = "http://localhost:8080/api/v1/auth/register";

export default function RegisterPage() {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    email: "",
    otp: "",
    username: "",
    password: "",
    confirmPassword: "",
    phoneNumber: "",
    address: "",
    gender: "MALE" as "MALE" | "FEMALE" | "OTHER",
  });

  const [isLoading, setIsLoading] = useState(false);
  const [errorText, setErrorText] = useState("");
  const [successText, setSuccessText] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  
  // OTP timer State
  const [timeLeft, setTimeLeft] = useState(0);
  const [resendLoading, setResendLoading] = useState(false);

  // OTP Array state for UI 6 boxes
  const [otpArray, setOtpArray] = useState<string[]>(Array(6).fill(""));
  const otpRefs = useRef<(HTMLInputElement | null)[]>([]);

  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (step === 2 && timeLeft > 0) {
      timer = setInterval(() => setTimeLeft((prev) => prev - 1), 1000);
    }
    return () => clearInterval(timer);
  }, [step, timeLeft]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrorText("");
    setSuccessText("");
  };

  const handleOtpChange = (index: number, value: string) => {
    if (isNaN(Number(value))) return;
    const newOtp = [...otpArray];
    newOtp[index] = value.substring(value.length - 1);
    setOtpArray(newOtp);
    setErrorText("");
    
    // Auto focus next
    if (value && index < 5) {
      otpRefs.current[index + 1]?.focus();
    }
    
    setFormData({ ...formData, otp: newOtp.join("") });
  };

  const handleOtpKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !otpArray[index] && index > 0) {
      otpRefs.current[index - 1]?.focus();
    }
  };

  // Validators
  const validateEmail = (email: string) => /^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$/.test(email);
  const validateUsername = (username: string) => /^[a-zA-Z0-9_]{4,20}$/.test(username);
  const validatePassword = (password: string) => /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,50}$/.test(password);
  const validatePhone = (phone: string) => /^(0|84)(3|5|7|8|9)\d{8}$/.test(phone);

  const startResendTimer = () => setTimeLeft(60);

  const handleNextStep1 = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.email) return setErrorText("Email is required");
    if (!validateEmail(formData.email)) return setErrorText("Invalid email format");
    
    setIsLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/stepone`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: formData.email }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Failed to send OTP");
      
      setSuccessText("We've sent an OTP to your email");
      setStep(2);
      startResendTimer();
    } catch (err: any) {
      setErrorText(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNextStep2 = async (e: React.FormEvent) => {
    e.preventDefault();
    const currentOtp = otpArray.join("");
    if (currentOtp.length !== 6) return setErrorText("Please enter the complete 6-digit OTP");
    
    setIsLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/steptwo`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: formData.email, otp: currentOtp }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Invalid OTP");
      
      setSuccessText("");
      setStep(3);
    } catch (err: any) {
      setErrorText(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleResendOtp = async () => {
    setResendLoading(true);
    setErrorText("");
    setSuccessText("");
    try {
      const res = await fetch(`${API_BASE_URL}/resendotp`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: formData.email }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Failed to resend OTP");
      
      setSuccessText("A new OTP has been sent to your email.");
      startResendTimer();
      setOtpArray(Array(6).fill(""));
      otpRefs.current[0]?.focus();
    } catch (err: any) {
      setErrorText(err.message);
    } finally {
      setResendLoading(false);
    }
  };

  const handleNextStep3 = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.username) return setErrorText("Username is required");
    if (!validateUsername(formData.username)) return setErrorText("Username: 4-20 chars, letters/numbers/underscores only");
    if (!formData.password) return setErrorText("Password is required");
    if (!validatePassword(formData.password)) return setErrorText("Password requires uppercase, lowercase, number, and special character for maximum security");
    if (formData.password !== formData.confirmPassword) return setErrorText("Passwords do not match");
    
    setIsLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/stepthree`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          email: formData.email, 
          username: formData.username, 
          password: formData.password 
        }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Failed to setup security");
      
      setSuccessText("");
      setStep(4);
    } catch (err: any) {
      setErrorText(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNextStep4 = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.phoneNumber) return setErrorText("Phone number is required");
    if (!validatePhone(formData.phoneNumber)) return setErrorText("Invalid Vietnam phone number (10 digits, starts with 0 or 84)");
    if (!formData.address) return setErrorText("Address is required");
    
    setIsLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/stepfour`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          email: formData.email, 
          phoneNumber: formData.phoneNumber, 
          address: formData.address,
          gender: formData.gender
        }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Registration failed");
      
      setSuccessText("");
      setStep(5); // completion step
    } catch (err: any) {
      setErrorText(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const animations: any = {
    initial: { opacity: 0, x: 15, filter: "blur(4px)" },
    animate: { opacity: 1, x: 0, filter: "blur(0px)" },
    exit: { opacity: 0, x: -15, filter: "blur(4px)" },
    transition: { duration: 0.5, ease: "easeOut" }
  };

  const stepTitles = [
    "Contact Detail",
    "Verification",
    "Security",
    "Personal Info",
    "Welcome"
  ];

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
          ALREADY A MEMBER? <Link href="/login" className="text-neutral-900 underline underline-offset-4 decoration-neutral-300 hover:decoration-neutral-900 transition-colors duration-300">SIGN IN</Link>
        </div>
      </nav>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col items-center justify-center p-6 md:p-12 relative overflow-hidden">
        {/* Abstract Background Gradient Blob */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-neutral-200/40 rounded-full blur-[120px] -z-10 pointer-events-none opacity-50" />
        
        <div className="w-full max-w-lg bg-white rounded-3xl shadow-[0_8px_40px_-12px_rgba(0,0,0,0.1)] border border-neutral-100 p-8 md:p-12 z-10 transition-all duration-500">
          
          {/* Progress Indicators */}
          {step < 5 && (
            <div className="mb-10 w-full relative">
              <div className="flex justify-between items-center relative z-10">
                {[1, 2, 3, 4].map((i) => (
                  <div key={i} className="flex flex-col items-center gap-2">
                    <div 
                      className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium transition-all duration-700 ease-in-out border-2 ${
                        step === i 
                          ? "bg-neutral-900 border-neutral-900 text-white shadow-lg scale-110" 
                          : step > i 
                            ? "bg-neutral-900 border-neutral-900 text-white" 
                            : "bg-white border-neutral-200 text-neutral-400"
                      }`}
                    >
                      {step > i ? <CheckCircle2 size={18} /> : i}
                    </div>
                  </div>
                ))}
              </div>
              {/* Progress Line */}
              <div className="absolute top-5 left-4 right-4 h-[2px] bg-neutral-100 -z-0 -translate-y-1/2">
                <div 
                  className="h-full bg-neutral-900 transition-all duration-700 ease-in-out"
                  style={{ width: `${((step - 1) / 3) * 100}%` }}
                />
              </div>
              <div className="mt-6 text-center">
                 <h2 className="text-sm uppercase tracking-widest text-neutral-500 font-semibold mb-1">Step {step} of 4</h2>
                 <h1 className="text-3xl font-light text-neutral-900 tracking-tight">{stepTitles[step - 1]}</h1>
              </div>
            </div>
          )}

          {/* Form Alerts: Error/Success */}
          <AnimatePresence mode="popLayout">
            {errorText && (
              <motion.div 
                initial={{ opacity: 0, y: -10 }} 
                animate={{ opacity: 1, y: 0 }} 
                exit={{ opacity: 0, y: -10 }} 
                className="mb-6 p-4 rounded-xl bg-red-50 border border-red-100 text-red-600 text-sm font-medium flex items-start gap-3"
              >
                <BadgeCheck size={18} className="mt-0.5 flex-shrink-0 text-red-500" />
                <span>{errorText}</span>
              </motion.div>
            )}
            {successText && (
              <motion.div 
                initial={{ opacity: 0, y: -10 }} 
                animate={{ opacity: 1, y: 0 }} 
                exit={{ opacity: 0, y: -10 }} 
                className="mb-6 p-4 rounded-xl bg-emerald-50 border border-emerald-100 text-emerald-700 text-sm font-medium flex items-start gap-3"
              >
                <CheckCircle2 size={18} className="mt-0.5 flex-shrink-0 text-emerald-600" />
                <span>{successText}</span>
              </motion.div>
            )}
          </AnimatePresence>

          {/* Form Views */}
          <div className="relative min-h-[300px]">
            <AnimatePresence mode="wait">
              {/* STEP 1: Email */}
              {step === 1 && (
                <motion.form key="step1" {...animations} onSubmit={handleNextStep1} className="flex flex-col h-full">
                  <div className="flex-1 space-y-6">
                    <p className="text-neutral-500 text-sm mb-6 leading-relaxed">Enter your email address to begin your journey with our exclusive collections and personalized recommendations.</p>
                    <div className="group relative">
                      <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-2 block">Email Address</label>
                      <div className="relative flex items-center">
                         <Mail className="absolute left-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                         <input 
                           name="email"
                           type="email" 
                           value={formData.email}
                           onChange={handleChange}
                           className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-4 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium text-base hover:bg-neutral-50"
                           placeholder="youremail@example.com"
                           required
                         />
                      </div>
                    </div>
                  </div>
                  <button 
                    disabled={isLoading}
                    type="submit" 
                    className="w-full mt-8 bg-neutral-900 hover:bg-neutral-800 text-white rounded-xl py-4 font-semibold tracking-wide flex items-center justify-center gap-2 transition-all active:scale-[0.98] disabled:opacity-70 group"
                  >
                    {isLoading ? <Loader2 className="animate-spin" size={20} /> : (
                      <>Continue <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" /></>
                    )}
                  </button>
                </motion.form>
              )}

              {/* STEP 2: OTP */}
              {step === 2 && (
                <motion.form key="step2" {...animations} onSubmit={handleNextStep2} className="flex flex-col h-full">
                  <div className="flex-1 space-y-8">
                    <p className="text-neutral-500 text-sm leading-relaxed text-center">
                      We have sent a 6-digit confirmation code to <br/>
                      <span className="font-semibold text-neutral-900">{formData.email}</span>
                    </p>
                    
                    <div className="flex justify-center gap-3">
                      {otpArray.map((val, idx) => (
                        <input
                          key={idx}
                          ref={(el) => { otpRefs.current[idx] = el; }}
                          type="text"
                          maxLength={1}
                          value={val}
                          onChange={(e) => handleOtpChange(idx, e.target.value)}
                          onKeyDown={(e) => handleOtpKeyDown(idx, e)}
                          className="w-12 h-14 md:w-14 md:h-16 text-center text-2xl font-semibold bg-neutral-50/50 border border-neutral-200 rounded-xl focus:outline-none focus:border-neutral-900 focus:ring-2 focus:ring-neutral-900/10 transition-all caret-neutral-900 text-neutral-900"
                        />
                      ))}
                    </div>

                    <div className="text-center mt-6">
                      <p className="text-sm text-neutral-500 font-medium">
                        Didn't receive the code? 
                        {timeLeft > 0 ? (
                          <span className="ml-1 text-neutral-900 font-semibold tracking-wide">Wait {timeLeft}s</span>
                        ) : (
                          <button 
                            type="button" 
                            onClick={handleResendOtp}
                            disabled={resendLoading}
                            className="ml-2 text-neutral-900 font-semibold underline underline-offset-4 hover:opacity-70 transition-opacity"
                          >
                            {resendLoading ? "Sending..." : "Resend OTP"}
                          </button>
                        )}
                      </p>
                    </div>
                  </div>

                  <button 
                    disabled={isLoading}
                    type="submit" 
                    className="w-full mt-8 bg-neutral-900 hover:bg-neutral-800 text-white rounded-xl py-4 font-semibold tracking-wide flex items-center justify-center gap-2 transition-all active:scale-[0.98] disabled:opacity-70 group"
                  >
                    {isLoading ? <Loader2 className="animate-spin" size={20} /> : (
                      <>Verify Code <ShieldCheck size={18} className="group-hover:scale-110 transition-transform" /></>
                    )}
                  </button>
                </motion.form>
              )}

              {/* STEP 3: Security */}
              {step === 3 && (
                <motion.form key="step3" {...animations} onSubmit={handleNextStep3} className="flex flex-col h-full">
                  <div className="flex-1 space-y-5">
                    
                    <div className="group relative">
                      <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-2 block">Username</label>
                      <div className="relative flex items-center">
                         <User className="absolute left-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                         <input 
                           name="username" type="text" value={formData.username} onChange={handleChange} required minLength={4} maxLength={20}
                           className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-4 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium hover:bg-neutral-50"
                           placeholder="Enter a unique username"
                         />
                      </div>
                    </div>

                    <div className="group relative">
                      <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-2 block">Password</label>
                      <div className="relative flex items-center">
                         <Lock className="absolute left-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                         <input 
                           name="password" type={showPassword ? "text" : "password"} value={formData.password} onChange={handleChange} required minLength={8}
                           className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-12 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium hover:bg-neutral-50"
                           placeholder="Create a strong password"
                         />
                         <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-4 text-neutral-400 hover:text-neutral-900 transition-colors">
                            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                         </button>
                      </div>
                      <p className="text-[11px] text-neutral-400 mt-2 font-medium">Must contain 8+ chars, upper & lowercase, num, special char (!@#$%^&+=)</p>
                    </div>

                    <div className="group relative">
                      <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-2 block">Confirm Password</label>
                      <div className="relative flex items-center">
                         <ShieldCheck className="absolute left-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                         <input 
                           name="confirmPassword" type={showConfirmPassword ? "text" : "password"} value={formData.confirmPassword} onChange={handleChange} required minLength={8}
                           className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-12 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium hover:bg-neutral-50"
                           placeholder="Repeat your password"
                         />
                         <button type="button" onClick={() => setShowConfirmPassword(!showConfirmPassword)} className="absolute right-4 text-neutral-400 hover:text-neutral-900 transition-colors">
                            {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                         </button>
                      </div>
                    </div>

                  </div>
                  <button 
                    disabled={isLoading} type="submit" 
                    className="w-full mt-8 bg-neutral-900 hover:bg-neutral-800 text-white rounded-xl py-4 font-semibold tracking-wide flex items-center justify-center gap-2 transition-all active:scale-[0.98] disabled:opacity-70"
                  >
                    {isLoading ? <Loader2 className="animate-spin" size={20} /> : "Secure Account"}
                  </button>
                </motion.form>
              )}

              {/* STEP 4: Personal Info */}
              {step === 4 && (
                <motion.form key="step4" {...animations} onSubmit={handleNextStep4} className="flex flex-col h-full">
                  <div className="flex-1 space-y-5">
                    
                    <div className="group relative">
                      <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-2 block">Phone Number</label>
                      <div className="relative flex items-center">
                         <Phone className="absolute left-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                         <input 
                           name="phoneNumber" type="tel" value={formData.phoneNumber} onChange={handleChange} required
                           className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-4 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium hover:bg-neutral-50"
                           placeholder="0912345678"
                         />
                      </div>
                    </div>

                    <div className="group relative">
                      <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-2 block">Delivery Address</label>
                      <div className="relative flex items-center">
                         <MapPin className="absolute left-4 top-4 text-neutral-400 group-focus-within:text-neutral-900 transition-colors" size={20} />
                         <input 
                           name="address" value={formData.address} onChange={handleChange} required
                           className="w-full bg-neutral-50/50 border border-neutral-200 rounded-xl py-4 pl-12 pr-4 text-neutral-900 placeholder:text-neutral-400 focus:outline-none focus:ring-2 focus:ring-neutral-900/10 focus:border-neutral-900 transition-all font-medium hover:bg-neutral-50"
                           placeholder="Str, Ward, Dist, City"
                         />
                      </div>
                    </div>

                    <div className="group relative">
                      <label className="text-xs uppercase tracking-wider text-neutral-500 font-semibold mb-3 block">Gender Identity</label>
                      <div className="flex bg-neutral-50/50 p-1 rounded-xl border border-neutral-200">
                        {["MALE", "FEMALE", "OTHER"].map((g) => (
                           <button
                             key={g}
                             type="button"
                             onClick={() => setFormData({ ...formData, gender: g as any })}
                             className={`flex-1 py-3 text-sm font-semibold rounded-lg transition-all capitalize ${
                               formData.gender === g 
                                 ? "bg-white text-neutral-900 shadow-sm ring-1 ring-neutral-200" 
                                 : "text-neutral-500 hover:text-neutral-900 hover:bg-neutral-100/50"
                             }`}
                           >
                             {g.toLowerCase()}
                           </button>
                        ))}
                      </div>
                    </div>

                  </div>
                  <button 
                    disabled={isLoading} type="submit" 
                    className="w-full mt-8 bg-neutral-900 hover:bg-neutral-800 text-white rounded-xl py-4 font-semibold tracking-wide flex items-center justify-center gap-2 transition-all active:scale-[0.98] disabled:opacity-70 group"
                  >
                    {isLoading ? <Loader2 className="animate-spin" size={20} /> : (
                      <>Complete Registration <CheckCircle2 size={18} className="group-hover:scale-110 transition-transform" /></>
                    )}
                  </button>
                </motion.form>
              )}

              {/* STEP 5: Success Output */}
              {step === 5 && (
                <motion.div key="step5" {...animations} className="flex flex-col items-center justify-center h-full py-10">
                   <div className="w-24 h-24 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mb-8 border-4 border-emerald-50">
                     <CheckCircle2 size={48} />
                   </div>
                   <h1 className="text-3xl font-light text-neutral-900 mb-4 text-center">Registration Complete</h1>
                   <p className="text-neutral-500 text-center mb-8 max-w-sm">
                     Welcome to Maison Élégante. Your account has been perfectly orchestrated. Discover your redefined look.
                   </p>
                   <Link 
                     href="/login" 
                     className="bg-neutral-900 hover:bg-neutral-800 text-white rounded-xl py-4 px-12 font-semibold tracking-wide flex items-center justify-center transition-all active:scale-[0.98]"
                   >
                     Go to Login
                   </Link>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
          
        </div>
        
        {/* Footer info minimalist */}
        <div className="mt-12 text-neutral-400 text-xs font-medium tracking-widest uppercase">
          © {new Date().getFullYear()} Maison Élégante. All Rights Reserved.
        </div>
      </main>
    </div>
  );
}
