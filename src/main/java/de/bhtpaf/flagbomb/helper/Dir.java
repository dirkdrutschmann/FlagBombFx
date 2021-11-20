package de.bhtpaf.flagbomb.helper;

public enum Dir {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    STAND;

    public static boolean isOpposite(Dir dir1, Dir dir2)
    {
        if (dir1 == null || dir2 == null)
        {
            return false;
        }

        if (dir1 == Dir.LEFT && dir2 == Dir.RIGHT)
        {
            return true;
        }
        else if (dir1 == Dir.DOWN && dir2 == Dir.UP)
        {
            return true;
        }
        else if (dir1 == Dir.RIGHT && dir2 == Dir.LEFT)
        {
            return true;
        }
        else if (dir1 == Dir.UP && dir2 == Dir.DOWN)
        {
            return true;
        }

        return false;
    }
}