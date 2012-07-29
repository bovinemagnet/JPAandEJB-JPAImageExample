package au.com.sup.jpatest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import au.com.sup.jpatest.exceptions.NonexistentEntityException;

/**
 * My code does not need documentation, it is too easy to read :D
 *
 * @author Paul Snow
 */
public class Main
{

    public Main()
    {
        Logger.getLogger(Main.class.getName()).setLevel(Level.ALL);
    }


    ImageExampleJpaController fatController = new ImageExampleJpaController();
    /**
     * Use the boring no NIO way to read a file.. Boring
     * @return
     * @throws IOException
     */
    public byte[] readImageOldWay(File file) throws IOException
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "readImageOldWay(File file)", file);
        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());

        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE)
        {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        Logger.getLogger(Main.class.getName()).exiting("Main", "readImageOldWay");
        return bytes;
    }

    /**
     * Use the cool NIO way to read a file, but I forgot how to do it.
     * @return
     * @throws IOException
     * @obsolete Couldn't remember how to get NIO working, so I used the old
     *           way.
     */
    public byte[] readImage() throws IOException
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "readImage");
        FileInputStream fi = new FileInputStream("images/mushroom.jpg");
        System.out.println("Avail : " + fi.available());
        FileChannel fic = fi.getChannel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024); // direct buffer

        long size = fic.size(), n = 0;

        // time to play the old game - clear, read, flip and write
        while (n < size)
        {
            buffer.clear(); // makes the buffer ready by resetting the pointers
            if (fic.read(buffer) < 0) // fill the buffer by reading from channel
                break;
            buffer.flip(); // makes the buffer writing the data just read
            // buffer.
            // n+= fcout.write(buffer);
        }

        fic.close();
        fi.close();
        Logger.getLogger(Main.class.getName()).exiting("Main", "readImage");
        return null;
    }

    /**
     * Lets write the file back to the file system using the old Non-NIO way. Boring.
     *
     * @param file duh! a file
     * @param data the contents of the file.
     * @throws IOException
     */
    public void writeFile(File file, byte[] data) throws IOException
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "writeFile", file);
        OutputStream fo = new FileOutputStream(file);
        // Write the data
        fo.write(data);
        // flush the file (down the toilet)
        fo.flush();
        // Close the door to keep the smell in.
        fo.close();
        Logger.getLogger(Main.class.getName()).exiting("Main", "writeFile");
    }
    /**
     *
     */
    public void generateCrap()
    {
        // we just fill up the database with images and pdf files.

    }

    /**
     *
     */
    public void addMushroomToJPA()
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "addMushroom");
        //ImageExampleJpaController fatController = new ImageExampleJpaController();
        /*
           * Put a JPG image into the db
           */
        File file = null;
        ImageExample myExample = new ImageExample();
        myExample.setMimeType("image/jpg");
        file = new File("images/mushroom.jpg");
        try
        {
            // Lets open an image file
            myExample.setImageFile(readImageOldWay(file));
        }
        catch (IOException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        fatController.create(myExample);

        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        Logger.getLogger(Main.class.getName()).exiting("Main", "addMushroom");
    }
    /**
     *
     */
    public void addPDF()
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "addPDF");
        File file = null;
        //ImageExampleJpaController fatController = new ImageExampleJpaController();

        /*
           * Put a PDF into the db
           */
        ImageExample myExampleTwo = new ImageExample();
        myExampleTwo.setMimeType("application/pdf");
        file = new File("images/studentexample.pdf");
        try
        {
            // Lets open an image file
            //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Call Read]");
            myExampleTwo.setImageFile(readImageOldWay(file));
            //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Property set]");
        }
        catch (IOException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        fatController.create(myExampleTwo);
        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());

        Logger.getLogger(Main.class.getName()).exiting("Main", "addPDF");
    }

    public void deleteByIterate()
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "deleteByIterate");
        /*
           * Clean up the db as OpenJPA's drop schema does not work properly for
           * me.
           */
        //ImageExampleJpaController fatController = new ImageExampleJpaController();
        List<ImageExample> images = fatController.findImageExampleEntities();
        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Clean Up]");
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        // Use the old style loop, as I can't remember the iterator way :)
        for (int i = 0; i < images.size(); i++)
        {
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Remove] ID:" + images.get(i).getId());
            try
            {
                fatController.destroy(images.get(i).getId());
            }
            catch (NonexistentEntityException e)
            {
                Logger.getLogger(Main.class.getName()).log(Level.INFO, e.getMessage());
            }
        }
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        Logger.getLogger(Main.class.getName()).exiting("Main", "deleteByIterate");
        // Thanks kids, I hope you liked it.
    }
    /**
     * Delete all the records using a named query.
     */
    public void deleteByQuery()
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "deleteByQuery");

        //ImageExampleJpaController fatController = new ImageExampleJpaController();
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        int returnInt  = fatController.deleteAll();
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        Logger.getLogger(Main.class.getName()).exiting("Main", "deleteByQuery");
    }


    public void dumpToFileSystem()
    {
        Logger.getLogger(Main.class.getName()).entering("Main", "dumptToFileSystem");

        File file = null;
        //ImageExampleJpaController fatController = new ImageExampleJpaController();
        /*
           * Lets read the images from JPA, and output them to the filesystem
           */
        // Get the list of images stored in the database.
        List<ImageExample> images = fatController.findImageExampleEntities();
        File outfile = null;
        // Go through the list returned, looking for PDF/JPG files.
        for (int i = 0; i < images.size(); i++)
        {
            if (images.get(i).getMimeType().equalsIgnoreCase("application/pdf"))
            {
                // write out the pdf file
                outfile = new File("out/test"+images.get(i).getId()+".pdf");
                try
                {
                    writeFile(outfile, images.get(i).getImageFile());
                }
                catch (IOException e)
                {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            else if (images.get(i).getMimeType().equalsIgnoreCase("image/jpg"))
            {
                // write out the pdf file
                outfile = new File("out/test"+images.get(i).getId()+".jpg");
                try
                {
                    writeFile(outfile, images.get(i).getImageFile());
                }
                catch (IOException e)
                {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            else
            {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Unknown file type");
            }
        }
        Logger.getLogger(Main.class.getName()).exiting("Main", "dumptToFileSystem");
    }


    public void runTest()
    {
        for (int i = 0; i < 1; i++)
        {
           addMushroomToJPA();
           addPDF();
        }
        /* dumpToFileSystem(); */
        deleteByIterate();
        // Now we delete all by the find query.

        //mainCourse.deleteByQuery();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // Turn on all logging.



        Main mainCourse = new Main();
                               mainCourse.runTest();



    }
}

