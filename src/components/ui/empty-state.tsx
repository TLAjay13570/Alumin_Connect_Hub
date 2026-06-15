import { LucideIcon, Inbox, Plus } from 'lucide-react';
import { Button } from './button';
import { cn } from '@/lib/utils';

interface EmptyStateProps {
  icon?: LucideIcon;
  title: string;
  description?: string;
  action?: {
    label: string;
    onClick: () => void;
  };
  className?: string;
  showAddIndicator?: boolean;
}

export function EmptyState({ 
  icon: Icon = Inbox,
  title,
  description,
  action,
  className,
  showAddIndicator = false
}: EmptyStateProps) {
  return (
    <div className={cn('flex flex-col items-center justify-center py-12 px-4 text-center', className)}>
      <div className="relative mb-5">
        <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary/10 to-primary/5 flex items-center justify-center">
          <div className="w-14 h-14 rounded-full bg-gradient-to-br from-primary/20 to-primary/10 flex items-center justify-center">
            <Icon className="w-7 h-7 text-primary/60" />
          </div>
        </div>
        {showAddIndicator && (
          <div className="absolute -top-1 -right-1 w-7 h-7 rounded-full bg-primary/10 flex items-center justify-center animate-pulse">
            <Plus className="w-4 h-4 text-primary" />
          </div>
        )}
      </div>
      <h3 className="text-lg font-semibold mb-2">{title}</h3>
      {description && (
        <p className="text-sm text-muted-foreground max-w-sm mb-5">{description}</p>
      )}
      {action && (
        <Button onClick={action.onClick} className="gap-2">
          {showAddIndicator && <Plus className="w-4 h-4" />}
          {action.label}
        </Button>
      )}
    </div>
  );
}

