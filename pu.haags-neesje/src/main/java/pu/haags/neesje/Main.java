package pu.haags.neesje;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class Main
{
public static final String OLD_ALL_DIRECTORY = "~/Boeken/Strips/00-uitzoeken/Grote verzameling";
public static final String OLD_ALL_DIRECTORY_PATTERN = "~/Boeken/Strips/00-uitzoeken/Grote verzameling/strips**";

public static class FileWalker extends SimpleFileVisitor<Path>
{
private Set<String> files = new HashSet<>();
private final PathMatcher matcher;
public FileWalker( String aPattern )
{
	super();
    matcher = FileSystems.getDefault().getPathMatcher( "glob:" + aPattern );
}

@Override
public FileVisitResult visitFile( Path aFile, BasicFileAttributes aAttributes )
{
	if ( aAttributes.isRegularFile() )
	{
		Path file = aFile.toAbsolutePath();
        if ( file != null && matcher.matches( file ) )
        {
        	files.add( file.toString() );
        }
	}
	return CONTINUE;
}
@Override
public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes aAttributes )
{
	System.out.format( "Directory gestart: %s%n", dir );
	return CONTINUE;
}
@Override
public FileVisitResult postVisitDirectory( Path dir, IOException exc )
{
	System.out.format( "Directory gedaan: %s%n", dir );
	return CONTINUE;
}

// If there is some error accessing the file, let the user know.
// If you don't override this method and an error occurs, an IOException is thrown.
@Override
public FileVisitResult visitFileFailed( Path file, IOException exc )
{
	System.err.println( "Fout in file " + file.toString() + " " + exc );
	return CONTINUE;
}
}

public static void main( String [] args ) throws IOException
{
	new Main().run();
}
public static String expandHome( String aPath )
{
	if ( aPath.startsWith( "~" + File.separator ) )
	{
		aPath = System.getProperty( "user.home" ) + aPath.substring( 1 );
	}
	else if ( aPath.startsWith( "~" ) )
	{
		// here you can implement reading homedir of other users if you care
		throw new UnsupportedOperationException( "Home dir expansion not implemented for explicit usernames" );
	}
	return aPath;
}

private void run() throws IOException
{
    Path startingDir = Paths.get( expandHome( OLD_ALL_DIRECTORY ) );
    String pattern = expandHome( OLD_ALL_DIRECTORY_PATTERN );

    FileWalker walker = new FileWalker( pattern );
    Files.walkFileTree( startingDir, walker );
}

}
