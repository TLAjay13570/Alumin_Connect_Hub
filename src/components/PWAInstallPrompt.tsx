import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Download, X } from 'lucide-react';

// Helper function to detect if device is mobile
const isMobileDevice = (): boolean => {
  // Check user agent for mobile devices
  const userAgent = navigator.userAgent || navigator.vendor || (window as any).opera;
  const mobileRegex = /android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i;
  
  // Check screen size (mobile devices typically have smaller screens)
  const isSmallScreen = window.innerWidth <= 768;
  
  // Check for touch capability
  const hasTouchScreen = 'ontouchstart' in window || navigator.maxTouchPoints > 0;
  
  // Consider it mobile if it matches user agent OR (has touch AND small screen)
  return mobileRegex.test(userAgent) || (hasTouchScreen && isSmallScreen);
};

const PWAInstallPrompt = () => {
  const [deferredPrompt, setDeferredPrompt] = useState<any>(null);
  const [showPrompt, setShowPrompt] = useState(false);
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    // Check if device is mobile once
    const mobile = isMobileDevice();
    setIsMobile(mobile);

    // Check service worker status for debugging (with delay to allow registration)
    if ('serviceWorker' in navigator) {
      setTimeout(() => {
        navigator.serviceWorker.getRegistration().then((registration) => {
          if (registration) {
            console.log('Service Worker is registered and active:', registration.active?.state);
          } else {
            // Don't warn - it might still be registering
            console.log('Service Worker registration pending...');
          }
        });
      }, 1000); // Wait 1 second for registration to complete
    }

    // Check if manifest is accessible
    fetch('/manifest.json')
      .then((res) => {
        if (res.ok) {
          console.log('Manifest.json is accessible');
        } else {
          console.error('Manifest.json is not accessible:', res.status);
        }
      })
      .catch((err) => {
        console.error('Error fetching manifest:', err);
      });

    const handler = (e: Event) => {
      console.log('beforeinstallprompt event fired', { isMobile: mobile });
      
      // Only prevent default and show custom prompt on mobile devices
      if (mobile) {
        // Prevent the mini-infobar from appearing on mobile
        e.preventDefault();
        // Store the event so it can be triggered later
        setDeferredPrompt(e);
        // Show install prompt to user
        setShowPrompt(true);
        console.log('Mobile: Custom install prompt will be shown');
      } else {
        // On desktop/PC/tablets, don't prevent default
        // This allows the browser's native install button to work
        // Still store the event in case user wants to programmatically trigger it
        setDeferredPrompt(e);
        // Don't show custom prompt on desktop
        setShowPrompt(false);
        console.log('Desktop: Browser native install button should appear');
      }
    };

    window.addEventListener('beforeinstallprompt', handler);

    // Log helpful info if event doesn't fire (for debugging)
    const timeout = setTimeout(() => {
      console.log('💡 PWA Installability Check:');
      console.log('- If beforeinstallprompt event hasn\'t fired, check:');
      console.log('  1. Is the app already installed? (Check Chrome menu > Apps)');
      console.log('  2. Is service worker active? (Check DevTools > Application > Service Workers)');
      console.log('  3. Is manifest valid? (Check DevTools > Application > Manifest)');
      console.log('  4. Try interacting with the page (click, scroll) - Chrome sometimes requires user engagement');
      console.log('  5. Wait a few seconds - Chrome may delay showing install button');
    }, 3000);

    return () => {
      window.removeEventListener('beforeinstallprompt', handler);
      clearTimeout(timeout);
    };
  }, []);

  const handleInstall = async () => {
    if (!deferredPrompt) return;

    // Show the install prompt
    deferredPrompt.prompt();

    // Wait for the user to respond to the prompt
    const { outcome } = await deferredPrompt.userChoice;

    console.log(`User response to the install prompt: ${outcome}`);

    // Clear the deferredPrompt
    setDeferredPrompt(null);
    setShowPrompt(false);
  };

  const handleDismiss = () => {
    setShowPrompt(false);
    // Remember dismissal for this session
    sessionStorage.setItem('pwa-prompt-dismissed', 'true');
  };

  // Don't show if:
  // - Not on mobile device
  // - Already dismissed
  // - Prompt not triggered
  // - Already installed (check if running as PWA)
  const isPWA = window.matchMedia('(display-mode: standalone)').matches;
  if (!isMobile || !showPrompt || sessionStorage.getItem('pwa-prompt-dismissed') || isPWA) {
    return null;
  }

  return (
    <Card className="fixed bottom-20 md:bottom-6 left-4 right-4 md:left-auto md:right-6 md:w-96 p-4 shadow-2xl border-2 border-primary/20 z-50 animate-in slide-in-from-bottom-5">
      <div className="flex items-start justify-between gap-3 mb-3">
        <div className="flex-1">
          <h3 className="font-bold text-lg mb-1">Install AlumniHub</h3>
          <p className="text-sm text-muted-foreground">
            Install our app for quick access and offline support
          </p>
        </div>
        <Button
          variant="ghost"
          size="icon"
          onClick={handleDismiss}
          className="h-8 w-8 flex-shrink-0"
        >
          <X className="w-4 h-4" />
        </Button>
      </div>
      <div className="flex gap-2">
        <Button onClick={handleInstall} className="flex-1">
          <Download className="w-4 h-4 mr-2" />
          Install App
        </Button>
        <Button variant="outline" onClick={handleDismiss} className="flex-1">
          Not Now
        </Button>
      </div>
    </Card>
  );
};

export default PWAInstallPrompt;

