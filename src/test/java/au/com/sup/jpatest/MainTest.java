package au.com.sup.jpatest;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * My code does not need documentation, it is too easy to read :D
 *
 * @author Paul Snow
 */
public class MainTest
{

    static Main myMain;

    @BeforeClass public static void onlyOnce()
    {
        myMain = new Main();
    }

    //  Simple test case should always be true.
    @Test
    public void testEmptyCollection() {
        Collection collection = new ArrayList();
        assertTrue(collection.isEmpty());
    }


   // @Test
   // public void testMain()
   // {
    //    au.com.sup.jpatest.Main myMain = new Main();
     //   myMain.runTest();
      //  assertTrue(true);
    //}


    @Test
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
            myExample.setImageFile(myMain.readImageOldWay(file));
        }
        catch (IOException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(ex.getCause().toString(), false);
        }

        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        myMain.fatController.create(myExample);

        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
        Logger.getLogger(Main.class.getName()).exiting("Main", "addMushroom");

        assertTrue(true);

    }
    @Test
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
            myExampleTwo.setImageFile(myMain.readImageOldWay(file));
            //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Property set]");
        }
        catch (IOException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(ex.getCause().toString(), false);
        }
        myMain.fatController.create(myExampleTwo);
        //Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());

        Logger.getLogger(Main.class.getName()).exiting("Main", "addPDF");
        assertTrue(true);
    }

}

