import { useState, useEffect, useMemo } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from '@/components/ui/pagination';
import { Search, Shield, Mail, Phone, Award, Grid3x3, Table2, Filter, X, GraduationCap } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';

interface AlumniUser {
  id: string;
  name: string;
  email: string;
  graduationYear: string;
  major: string;
  isMentor: boolean;
  universityId: string;
  phone?: string;
  location?: string;
}

const AdminMentorList = () => {
  const { user } = useAuth();
  const [mentors, setMentors] = useState<AlumniUser[]>([]);
  const [viewMode, setViewMode] = useState<'cards' | 'table'>('cards');
  const [currentPage, setCurrentPage] = useState(1);
  const [isFilterModalOpen, setIsFilterModalOpen] = useState(false);
  const [filters, setFilters] = useState({
    name: '',
    email: '',
    major: '',
    graduationYear: '',
  });
  const [tempFilters, setTempFilters] = useState(filters);
  const itemsPerPage = 12;

  useEffect(() => {
    // Load users and filter mentors
    const users = JSON.parse(localStorage.getItem(`alumni_users_${user?.universityId}`) || '[]');
    const mentorUsers = users.filter((u: AlumniUser) => u.isMentor);
    
    // Load additional profile data for mentors
    const mentorsWithProfile = mentorUsers.map((mentor: AlumniUser) => {
      const profileData = JSON.parse(localStorage.getItem(`profile_data_${mentor.id}`) || 'null');
      return {
        ...mentor,
        phone: mentor.phone || profileData?.phone || '',
        location: mentor.location || profileData?.location || '',
      };
    });
    
    // Add some dummy mentors if none exist
    if (mentorsWithProfile.length === 0) {
      const dummyMentors: AlumniUser[] = [
        {
          id: 'm1',
          name: 'Dr. Sarah Williams',
          email: 'sarah.w@example.com',
          graduationYear: '2010',
          major: 'Computer Science',
          isMentor: true,
          universityId: user?.universityId || '',
        },
        {
          id: 'm2',
          name: 'Michael Chen',
          email: 'michael.c@example.com',
          graduationYear: '2012',
          major: 'Engineering',
          isMentor: true,
          universityId: user?.universityId || '',
        },
        {
          id: 'm3',
          name: 'Emily Rodriguez',
          email: 'emily.r@example.com',
          graduationYear: '2015',
          major: 'Business Administration',
          isMentor: true,
          universityId: user?.universityId || '',
        },
        {
          id: 'm4',
          name: 'David Kim',
          email: 'david.k@example.com',
          graduationYear: '2013',
          major: 'Data Science',
          isMentor: true,
          universityId: user?.universityId || '',
        },
        {
          id: 'm5',
          name: 'Jessica Martinez',
          email: 'jessica.m@example.com',
          graduationYear: '2011',
          major: 'Marketing',
          isMentor: true,
          universityId: user?.universityId || '',
        },
      ];
      setMentors(dummyMentors);
    } else {
      setMentors(mentorsWithProfile);
    }
  }, [user?.universityId]);

  // Filter mentors based on all filter criteria
  const filteredMentors = useMemo(() => {
    return mentors.filter(mentor => {
      const nameMatch = !filters.name || mentor.name.toLowerCase().includes(filters.name.toLowerCase());
      const emailMatch = !filters.email || mentor.email.toLowerCase().includes(filters.email.toLowerCase());
      const majorMatch = !filters.major || mentor.major.toLowerCase().includes(filters.major.toLowerCase());
      const yearMatch = !filters.graduationYear || mentor.graduationYear.includes(filters.graduationYear);
      
      return nameMatch && emailMatch && majorMatch && yearMatch;
    });
  }, [mentors, filters]);

  // Pagination
  const totalPages = Math.ceil(filteredMentors.length / itemsPerPage);
  const paginatedMentors = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    return filteredMentors.slice(start, end);
  }, [filteredMentors, currentPage, itemsPerPage]);

  const handleFilterChange = (key: string, value: string) => {
    setTempFilters((prev) => ({ ...prev, [key]: value }));
  };

  const applyFilters = () => {
    setFilters(tempFilters);
    setCurrentPage(1);
    setIsFilterModalOpen(false);
  };

  const clearFilters = () => {
    const emptyFilters = {
      name: '',
      email: '',
      major: '',
      graduationYear: '',
    };
    setTempFilters(emptyFilters);
    setFilters(emptyFilters);
    setCurrentPage(1);
  };

  const getActiveFilterCount = () => {
    let count = 0;
    if (filters.name) count++;
    if (filters.email) count++;
    if (filters.major) count++;
    if (filters.graduationYear) count++;
    return count;
  };

  // Sync tempFilters when modal opens
  useEffect(() => {
    if (isFilterModalOpen) {
      setTempFilters(filters);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isFilterModalOpen]);

  return (
    <div className="space-y-6">
      <Card className="p-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="flex-1">
            <h2 className="text-2xl font-bold mb-2">Active Mentors</h2>
            <p className="text-sm text-muted-foreground">
              Alumni who are available to mentor current students and other alumni
            </p>
          </div>
          <div className="flex items-center gap-4">
            <Badge variant="outline" className="text-lg px-4 py-2 flex items-center gap-2">
              <Shield className="w-4 h-4" />
              {filteredMentors.length} Mentors
            </Badge>
            <div className="flex items-center gap-2 border rounded-lg p-1">
              <Button
                variant={viewMode === 'cards' ? 'default' : 'ghost'}
                size="sm"
                onClick={() => setViewMode('cards')}
                className="h-8"
              >
                <Grid3x3 className="w-4 h-4" />
              </Button>
              <Button
                variant={viewMode === 'table' ? 'default' : 'ghost'}
                size="sm"
                onClick={() => setViewMode('table')}
                className="h-8"
              >
                <Table2 className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </div>

        {/* Filter Button */}
        <div className="flex items-center justify-between mb-6">
          <Dialog open={isFilterModalOpen} onOpenChange={setIsFilterModalOpen}>
            <DialogTrigger asChild>
              <Button variant="outline" size="sm">
                <Filter className="w-4 h-4 mr-2" />
                Filters
                {getActiveFilterCount() > 0 && (
                  <Badge variant="secondary" className="ml-2">
                    {getActiveFilterCount()}
                  </Badge>
                )}
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl">
              <DialogHeader>
                <DialogTitle>Filter Mentors</DialogTitle>
              </DialogHeader>
              <div className="space-y-4 mt-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>Name</Label>
                    <div className="relative">
                      <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                      <Input
                        placeholder="Search name..."
                        value={tempFilters.name}
                        onChange={(e) => handleFilterChange('name', e.target.value)}
                        className="pl-9"
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label>Email</Label>
                    <div className="relative">
                      <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                      <Input
                        placeholder="Search email..."
                        value={tempFilters.email}
                        onChange={(e) => handleFilterChange('email', e.target.value)}
                        className="pl-9"
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label>Major</Label>
                    <Input
                      placeholder="Search major..."
                      value={tempFilters.major}
                      onChange={(e) => handleFilterChange('major', e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Graduation Year</Label>
                    <Input
                      placeholder="Search year..."
                      value={tempFilters.graduationYear}
                      onChange={(e) => handleFilterChange('graduationYear', e.target.value)}
                    />
                  </div>
                </div>
                <div className="flex items-center justify-between pt-4 border-t">
                  <Button variant="outline" onClick={clearFilters}>
                    <X className="w-4 h-4 mr-2" />
                    Clear All
                  </Button>
                  <div className="flex gap-2">
                    <Button variant="outline" onClick={() => setIsFilterModalOpen(false)}>
                      Cancel
                    </Button>
                    <Button onClick={applyFilters}>
                      Apply Filters
                    </Button>
                  </div>
                </div>
              </div>
            </DialogContent>
          </Dialog>
          {getActiveFilterCount() > 0 && (
            <Button variant="ghost" size="sm" onClick={clearFilters} className="text-muted-foreground">
              <X className="w-4 h-4 mr-1" />
              Clear Filters
            </Button>
          )}
        </div>
      </Card>

      {/* Cards View */}
      {viewMode === 'cards' && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {paginatedMentors.length === 0 ? (
              <Card className="p-10 text-center col-span-full border-dashed border-2 bg-gradient-to-br from-muted/30 via-background to-muted/30">
                <div className="flex flex-col items-center justify-center">
                  <div className="relative mb-5">
                    <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary/10 to-primary/5 flex items-center justify-center">
                      <div className="w-14 h-14 rounded-full bg-gradient-to-br from-primary/20 to-primary/10 flex items-center justify-center">
                        <GraduationCap className="w-7 h-7 text-primary/60" />
                      </div>
                    </div>
                  </div>
                  <h3 className="text-lg font-semibold mb-2">No Mentors Found</h3>
                  <p className="text-sm text-muted-foreground max-w-sm">
                    {getActiveFilterCount() > 0 ? 'No mentors match your filters. Try adjusting your search.' : 'No alumni have registered as mentors yet.'}
                  </p>
                </div>
              </Card>
            ) : (
              paginatedMentors.map(mentor => (
                <Card key={mentor.id} className="p-6 hover:shadow-lg transition-shadow">
                  <div className="flex items-start gap-4 mb-4">
                    <div className="w-16 h-16 rounded-full bg-gradient-to-br from-primary to-secondary flex items-center justify-center text-white font-bold text-xl flex-shrink-0">
                      {mentor.name.split(' ').map(n => n[0]).join('')}
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-semibold text-lg mb-1 truncate">{mentor.name}</h3>
                      <p className="text-sm text-muted-foreground truncate">{mentor.major}</p>
                      <Badge variant="outline" className="mt-2">
                        <Award className="w-3 h-3 mr-1" />
                        Class of {mentor.graduationYear}
                      </Badge>
                    </div>
                  </div>
                  
                  <div className="space-y-2 mb-4">
                    <div className="flex items-center gap-2 text-sm">
                      <Mail className="w-4 h-4 text-muted-foreground flex-shrink-0" />
                      <span className="truncate">{mentor.email}</span>
                    </div>
                    {mentor.phone && (
                      <div className="flex items-center gap-2 text-sm">
                        <Phone className="w-4 h-4 text-muted-foreground flex-shrink-0" />
                        <span className="truncate">{mentor.phone}</span>
                      </div>
                    )}
                  </div>

                  <div className="flex gap-2">
                    <Button size="sm" className="flex-1">
                      <Mail className="w-4 h-4 mr-2" />
                      Contact
                    </Button>
                    <Button size="sm" variant="outline" className="flex-1">
                      View Profile
                    </Button>
                  </div>
                </Card>
              ))
            )}
          </div>
          
          {/* Pagination for Cards View */}
          {totalPages > 1 && (
            <div className="mt-6 flex justify-center">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <PaginationPrevious
                      onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
                      className={currentPage === 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                    />
                  </PaginationItem>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                    <PaginationItem key={page}>
                      <PaginationLink
                        onClick={() => setCurrentPage(page)}
                        isActive={currentPage === page}
                        className="cursor-pointer"
                      >
                        {page}
                      </PaginationLink>
                    </PaginationItem>
                  ))}
                  <PaginationItem>
                    <PaginationNext
                      onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
                      className={currentPage === totalPages ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          )}
        </>
      )}

      {/* Table View */}
      {viewMode === 'table' && (
        <Card className="p-4">
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Phone</TableHead>
                  <TableHead>Major</TableHead>
                  <TableHead>Graduation Year</TableHead>
                  <TableHead>Location</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {paginatedMentors.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} className="py-12">
                      <div className="flex flex-col items-center justify-center">
                        <div className="w-16 h-16 rounded-full bg-gradient-to-br from-primary/10 to-primary/5 flex items-center justify-center mb-4">
                          <div className="w-12 h-12 rounded-full bg-gradient-to-br from-primary/20 to-primary/10 flex items-center justify-center">
                            <Search className="w-5 h-5 text-primary/60" />
                          </div>
                        </div>
                        <h4 className="font-medium mb-1">No Mentors Found</h4>
                        <p className="text-sm text-muted-foreground">
                          {getActiveFilterCount() > 0 ? 'No mentors match your filters.' : 'No mentors available'}
                        </p>
                      </div>
                    </TableCell>
                  </TableRow>
                ) : (
                  paginatedMentors.map(mentor => (
                    <TableRow key={mentor.id}>
                      <TableCell className="font-medium">{mentor.name}</TableCell>
                      <TableCell>{mentor.email}</TableCell>
                      <TableCell>{mentor.phone || '-'}</TableCell>
                      <TableCell>{mentor.major}</TableCell>
                      <TableCell>
                        <Badge variant="outline">
                          <Award className="w-3 h-3 mr-1" />
                          {mentor.graduationYear}
                        </Badge>
                      </TableCell>
                      <TableCell>{mentor.location || '-'}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="mt-4 flex justify-center">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <PaginationPrevious
                      onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
                      className={currentPage === 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                    />
                  </PaginationItem>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                    <PaginationItem key={page}>
                      <PaginationLink
                        onClick={() => setCurrentPage(page)}
                        isActive={currentPage === page}
                        className="cursor-pointer"
                      >
                        {page}
                      </PaginationLink>
                    </PaginationItem>
                  ))}
                  <PaginationItem>
                    <PaginationNext
                      onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
                      className={currentPage === totalPages ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          )}
        </Card>
      )}
    </div>
  );
};

export default AdminMentorList;

