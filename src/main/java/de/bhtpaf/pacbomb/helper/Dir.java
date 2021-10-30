package de.bhtpaf.pacbomb.helper;

public enum Dir {
    left,
    right,
    up,
    down,
    stand;

    public static boolean isOpposite(Dir dir1, Dir dir2)
    {
        if (dir1 == null || dir2 == null)
        {
            return false;
        }

        if (dir1 == Dir.left && dir2 == Dir.right)
        {
            return true;
        }
        else if (dir1 == Dir.down && dir2 == Dir.up)
        {
            return true;
        }
        else if (dir1 == Dir.right && dir2 == Dir.left)
        {
            return true;
        }
        else if (dir1 == Dir.up && dir2 == Dir.down)
        {
            return true;
        }

        return false;
    }
}