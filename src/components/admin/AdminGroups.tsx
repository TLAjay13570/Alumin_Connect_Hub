import { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useGroups } from '@/contexts/GroupsContext';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { UsersRound, Edit, Trash2, Plus, Users } from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import GroupModal from '@/components/GroupModal';

const AdminGroups = () => {
  const { user } = useAuth();
  const { groups, deleteGroup } = useGroups();
  const { toast } = useToast();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGroup, setEditingGroup] = useState<any>(null);

  const handleDelete = (groupId: string) => {
    if (window.confirm('Are you sure you want to delete this group? This action cannot be undone.')) {
      deleteGroup(groupId);
      toast({
        title: 'Group deleted',
        description: 'The group has been removed',
      });
    }
  };

  const handleEdit = (group: any) => {
    setEditingGroup(group);
    setIsModalOpen(true);
  };

  return (
    <div className="space-y-6">
      <Card className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex-1">
            <h2 className="text-2xl font-bold mb-2">Group Management</h2>
            <p className="text-sm text-muted-foreground">
              Create and manage alumni groups
            </p>
          </div>
          <Button onClick={() => setIsModalOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            Create Group
          </Button>
        </div>

        {/* Statistics */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
          <div className="p-4 rounded-lg border bg-card">
            <p className="text-2xl font-bold">{groups.length}</p>
            <p className="text-sm text-muted-foreground">Total Groups</p>
          </div>
          <div className="p-4 rounded-lg border bg-card">
            <p className="text-2xl font-bold">{groups.reduce((sum, g) => sum + g.memberCount, 0)}</p>
            <p className="text-sm text-muted-foreground">Total Members</p>
          </div>
          <div className="p-4 rounded-lg border bg-card">
            <p className="text-2xl font-bold">{Math.round(groups.reduce((sum, g) => sum + g.memberCount, 0) / groups.length) || 0}</p>
            <p className="text-sm text-muted-foreground">Avg Members/Group</p>
          </div>
        </div>
      </Card>

      {/* Groups Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {groups.length === 0 ? (
          <Card className="p-10 text-center col-span-full border-dashed border-2 bg-gradient-to-br from-muted/30 via-background to-muted/30">
            <div className="flex flex-col items-center justify-center">
              <div className="relative mb-5">
                <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary/10 to-primary/5 flex items-center justify-center">
                  <div className="w-14 h-14 rounded-full bg-gradient-to-br from-primary/20 to-primary/10 flex items-center justify-center">
                    <UsersRound className="w-7 h-7 text-primary/60" />
                  </div>
                </div>
                <div className="absolute -top-1 -right-1 w-7 h-7 rounded-full bg-primary/10 flex items-center justify-center animate-pulse">
                  <Plus className="w-4 h-4 text-primary" />
                </div>
              </div>
              <h3 className="text-lg font-semibold mb-2">No Groups Yet</h3>
              <p className="text-sm text-muted-foreground max-w-sm mb-5">
                Create alumni groups to foster community engagement around shared interests or graduation years.
              </p>
              <Button onClick={() => setIsModalOpen(true)} className="gap-2">
                <Plus className="w-4 h-4" />
                Create First Group
              </Button>
            </div>
          </Card>
        ) : (
          groups.map(group => (
            <Card key={group.id} className="p-6 hover:shadow-lg transition-shadow">
              {group.image && (
                <img 
                  src={group.image} 
                  alt={group.name}
                  className="w-full h-32 object-cover rounded-lg mb-4"
                />
              )}
              
              <h3 className="font-semibold text-lg mb-2">{group.name}</h3>
              <p className="text-sm text-muted-foreground mb-4 line-clamp-2">{group.description}</p>
              
              <div className="flex items-center gap-2 mb-4">
                <Badge variant="secondary">{group.category}</Badge>
                <div className="flex items-center gap-1 text-sm text-muted-foreground">
                  <Users className="w-4 h-4" />
                  <span>{group.memberCount} members</span>
                </div>
              </div>

              <div className="flex gap-2">
                <Button size="sm" variant="outline" onClick={() => handleEdit(group)} className="flex-1">
                  <Edit className="w-4 h-4 mr-2" />
                  Edit
                </Button>
                <Button 
                  size="sm" 
                  variant="outline"
                  onClick={() => handleDelete(group.id)}
                  className="flex-1 text-destructive hover:text-destructive"
                >
                  <Trash2 className="w-4 h-4 mr-2" />
                  Delete
                </Button>
              </div>
            </Card>
          ))
        )}
      </div>

      <GroupModal
        open={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingGroup(null);
        }}
        group={editingGroup}
      />
    </div>
  );
};

export default AdminGroups;

