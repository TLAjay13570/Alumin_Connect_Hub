import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet';
import { Filter, X, Search } from 'lucide-react';
import { Checkbox } from '@/components/ui/checkbox';

interface PostFilterProps {
  onFilterChange: (filters: FilterOptions) => void;
  activeFilters: FilterOptions;
}

export interface FilterOptions {
  postTypes: string[];
  tags: string[];
  universities: string[];
  searchText: string;
}

const postTypes = [
  { value: 'text', label: 'Text Posts' },
  { value: 'image', label: 'Image Posts' },
  { value: 'video', label: 'Video Posts' },
  { value: 'job', label: 'Job Opportunities' },
  { value: 'announcement', label: 'Announcements' },
];

const postTags = [
  { value: 'success-story', label: 'Success Story', icon: '🏆' },
  { value: 'career-milestone', label: 'Career Milestone', icon: '📈' },
  { value: 'achievement', label: 'Achievement', icon: '⭐' },
  { value: 'learning', label: 'Learning Journey', icon: '📚' },
  { value: 'volunteering', label: 'Volunteering', icon: '❤️' },
];

const universities = [
  'MIT',
  'Stanford',
  'Harvard',
  'Berkeley',
  'Yale',
  'Princeton',
  'Cornell',
  'Columbia',
  'Duke',
  'Northwestern',
  'Penn',
  'Brown',
  'UCLA',
];

const PostFilter = ({ onFilterChange, activeFilters }: PostFilterProps) => {
  const [localFilters, setLocalFilters] =
    useState<FilterOptions>(activeFilters);
  const [isOpen, setIsOpen] = useState(false);

  const handleTogglePostType = (type: string) => {
    const newTypes = localFilters.postTypes.includes(type)
      ? localFilters.postTypes.filter((t) => t !== type)
      : [...localFilters.postTypes, type];
    setLocalFilters({ ...localFilters, postTypes: newTypes });
  };

  const handleToggleTag = (tag: string) => {
    const newTags = localFilters.tags.includes(tag)
      ? localFilters.tags.filter((t) => t !== tag)
      : [...localFilters.tags, tag];
    setLocalFilters({ ...localFilters, tags: newTags });
  };

  const handleToggleUniversity = (university: string) => {
    const newUniversities = localFilters.universities.includes(university)
      ? localFilters.universities.filter((u) => u !== university)
      : [...localFilters.universities, university];
    setLocalFilters({ ...localFilters, universities: newUniversities });
  };

  const handleApplyFilters = () => {
    onFilterChange(localFilters);
    setIsOpen(false);
  };

  const handleClearFilters = () => {
    const emptyFilters: FilterOptions = {
      postTypes: [],
      tags: [],
      universities: [],
      searchText: '',
    };
    setLocalFilters(emptyFilters);
    onFilterChange(emptyFilters);
  };

  const activeFilterCount =
    localFilters.postTypes.length +
    localFilters.tags.length +
    localFilters.universities.length +
    (localFilters.searchText ? 1 : 0);

  return (
    <Sheet
      open={isOpen}
      onOpenChange={setIsOpen}
    >
      <SheetTrigger asChild>
        <Button
          variant="outline"
          className="gap-1.5 sm:gap-2 relative h-9 sm:h-10 px-2.5 sm:px-4 text-sm"
        >
          <Filter className="w-4 h-4" />
          <span className="hidden sm:inline">Filter Posts</span>
          <span className="sm:hidden">Filter</span>
          {activeFilterCount > 0 && (
            <Badge className="ml-0.5 sm:ml-1 h-4 w-4 sm:h-5 sm:w-5 p-0 flex items-center justify-center rounded-full bg-primary text-primary-foreground text-[10px] sm:text-xs">
              {activeFilterCount}
            </Badge>
          )}
        </Button>
      </SheetTrigger>
      <SheetContent className="w-full sm:max-w-md overflow-y-auto">
        <SheetHeader>
          <SheetTitle className="text-xl font-bold">Filter Posts</SheetTitle>
          <SheetDescription>
            Refine your feed by post type, tags, university, and more
          </SheetDescription>
        </SheetHeader>

        <div className="space-y-6 py-6">
          {/* Search Text */}
          <div className="space-y-2">
            <Label className="text-sm font-semibold">Search in Posts</Label>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Search keywords..."
                value={localFilters.searchText}
                onChange={(e) =>
                  setLocalFilters({
                    ...localFilters,
                    searchText: e.target.value,
                  })
                }
                className="pl-9"
              />
            </div>
          </div>

          {/* Post Types */}
          <div className="space-y-3">
            <Label className="text-sm font-semibold">Post Type</Label>
            <div className="space-y-2">
              {postTypes.map((type) => (
                <div
                  key={type.value}
                  className="flex items-center space-x-2"
                >
                  <Checkbox
                    id={`type-${type.value}`}
                    checked={localFilters.postTypes.includes(type.value)}
                    onCheckedChange={() => handleTogglePostType(type.value)}
                  />
                  <label
                    htmlFor={`type-${type.value}`}
                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 cursor-pointer"
                  >
                    {type.label}
                  </label>
                </div>
              ))}
            </div>
          </div>

          {/* Tags */}
          <div className="space-y-3">
            <Label className="text-sm font-semibold">Tags</Label>
            <div className="flex flex-wrap gap-2">
              {postTags.map((tag) => (
                <Badge
                  key={tag.value}
                  variant={
                    localFilters.tags.includes(tag.value)
                      ? 'default'
                      : 'outline'
                  }
                  className="cursor-pointer transition-all hover:scale-105"
                  onClick={() => handleToggleTag(tag.value)}
                >
                  <span className="mr-1">{tag.icon}</span>
                  {tag.label}
                </Badge>
              ))}
            </div>
          </div>

          {/* Universities */}
          <div className="space-y-3">
            <Label className="text-sm font-semibold">University</Label>
            <div className="space-y-2 max-h-48 overflow-y-auto pr-2">
              {universities.map((university) => (
                <div
                  key={university}
                  className="flex items-center space-x-2"
                >
                  <Checkbox
                    id={`university-${university}`}
                    checked={localFilters.universities.includes(university)}
                    onCheckedChange={() => handleToggleUniversity(university)}
                  />
                  <label
                    htmlFor={`university-${university}`}
                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 cursor-pointer"
                  >
                    {university}
                  </label>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-3 pt-4 border-t">
          <Button
            variant="outline"
            onClick={handleClearFilters}
            className="flex-1"
            disabled={activeFilterCount === 0}
          >
            <X className="w-4 h-4 mr-2" />
            Clear All
          </Button>
          <Button
            onClick={handleApplyFilters}
            className="flex-1"
          >
            Apply Filters
          </Button>
        </div>
      </SheetContent>
    </Sheet>
  );
};

export default PostFilter;
