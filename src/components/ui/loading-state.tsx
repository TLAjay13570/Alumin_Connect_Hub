import { Loader2 } from 'lucide-react';
import { cn } from '@/lib/utils';

interface LoadingStateProps {
  message?: string;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
}

export function LoadingState({ 
  message = 'Loading...', 
  className,
  size = 'md' 
}: LoadingStateProps) {
  const sizeClasses = {
    sm: { outer: 'w-10 h-10', inner: 'w-8 h-8', icon: 'w-4 h-4' },
    md: { outer: 'w-14 h-14', inner: 'w-12 h-12', icon: 'w-5 h-5' },
    lg: { outer: 'w-18 h-18', inner: 'w-16 h-16', icon: 'w-6 h-6' },
  };

  const sizes = sizeClasses[size];

  return (
    <div className={cn('flex flex-col items-center justify-center py-12', className)}>
      <div className="relative mb-4">
        <div className={cn('rounded-full border-4 border-primary/20 animate-pulse', sizes.outer)} />
        <div className={cn('rounded-full border-4 border-t-primary border-transparent animate-spin absolute inset-0', sizes.outer)} />
        <Loader2 className={cn('text-primary absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2', sizes.icon)} />
      </div>
      {message && (
        <p className="text-sm text-muted-foreground animate-pulse">{message}</p>
      )}
      <div className="flex gap-1 mt-2">
        <div className="w-1.5 h-1.5 rounded-full bg-primary animate-bounce" style={{ animationDelay: '0ms' }} />
        <div className="w-1.5 h-1.5 rounded-full bg-primary animate-bounce" style={{ animationDelay: '150ms' }} />
        <div className="w-1.5 h-1.5 rounded-full bg-primary animate-bounce" style={{ animationDelay: '300ms' }} />
      </div>
    </div>
  );
}

interface LoadingSpinnerProps {
  className?: string;
  size?: 'sm' | 'md' | 'lg';
}

export function LoadingSpinner({ className, size = 'md' }: LoadingSpinnerProps) {
  const sizeClasses = {
    sm: 'h-4 w-4',
    md: 'h-6 w-6',
    lg: 'h-8 w-8',
  };

  return (
    <Loader2 className={cn('animate-spin', sizeClasses[size], className)} />
  );
}

interface PageLoadingProps {
  message?: string;
}

export function PageLoading({ message = 'Loading...' }: PageLoadingProps) {
  return (
    <div className="min-h-[400px] flex flex-col items-center justify-center">
      <div className="inline-block w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      <p className="mt-4 text-muted-foreground">{message}</p>
    </div>
  );
}

