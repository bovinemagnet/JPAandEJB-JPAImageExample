# Loading And Image Into DB #

Originally I wrote this to prove that spring wasn't needed, and if you wrote for JPA you could deploy in either tomcat web server or within a J2EE Container.  Well a few years have passed, nobody seems to use J2EE any more, and everyone used spring (I lost that argument, when it was just APO and DI for J2EE, now spring is massive and cool.)

## Running The Example ##

I originally just ran this in InteliJ, but I have updated it to run from maven.


The default JPA provider is OpenJPA.

````bash
mvn test
````

You should see some test output such as

````java
Running au.com.sup.jpatest.MainTest
[main] INFO au.com.sup.jpatest.ImageExampleJpaController - ImageExampleJPAController.constructor
[main] INFO au.com.sup.jpatest.ImageExampleJpaController - ImageExampleJPAController.create
25  H2JPAImageOpenJPA  INFO   [main] openjpa.Runtime - Starting OpenJPA 3.2.0
62  H2JPAImageOpenJPA  INFO   [main] openjpa.jdbc.JDBC - Using dictionary class "org.apache.openjpa.jdbc.sql.H2Dictionary".
194  H2JPAImageOpenJPA  INFO   [main] openjpa.jdbc.JDBC - Connected to H2 version 2.1 using JDBC driver H2 JDBC Driver version 2.1.210 (2022-01-17).
308  H2JPAImageOpenJPA  TRACE  [main] openjpa.jdbc.SQL - <t 1843743552, conn 1139609587> executing prepstmnt 15094126 
SELECT SEQUENCE_SCHEMA, SEQUENCE_NAME 
    FROM INFORMATION_SCHEMA.SEQUENCES 
````


## BLOG ##

I originally wrote this as a blog back in 2009, it was to quickly prove that it was pretty easy to load an image into a blob in the database using JPA.

Supporting code for the blog entry http://jpaandejb.wordpress.com/2009/10/07/loading-an-image-into-a-database-using-jpa/

This is the blog entry:



Loading an image (or any file)  into a database via JPA is quite easy actually, here is some code to help you out.  Please note that this is quick dirty code, and you might consider a lot of changes were you to use it.  But the idea is there, and will allow you  see how it goes.

Firstly one needs to create their entity class

````java
@Entity
public class ImageExample implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String MimeType;  // Useful to store the mime type incase you want to send it back via a servlet.

  // We mark up the byte array with a long object datatype, setting the fetch type to lazy.
  @Lob
  @Basic(fetch=FetchType.LAZY) // this gets ignored anyway, but it is recommended for blobs
  protected  byte[]  imageFile;
````

Using a simple bit of Java code taken from the Java Developers Almanac, we read the file and return it as a byte array.

````java
public byte[] readImageOldWay(File file) throws IOException
{
  Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
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
  return bytes;
}
````

We create an entity object and use the entity manager.

````java
/*
 * Put a JPG image into the db
 */
ImageExample myExample = new ImageExample();
myExample.setMimeType("image/jpg");
file = new File("images/mushroom.jpg");
try
{
  // Lets open an image file
  myExample.setImageFile(mainCourse.readImageOldWay(file));
}
catch (IOException ex)
{
  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
}
````

Then we persist the entity object.

````java
fatController.create(myExample);
````

This works easily as well for PDF documents

````java
/*
 * Put a PDF into the db
 */
 ImageExample myExampleTwo = new ImageExample();
 myExampleTwo.setMimeType("application/pdf");
 file = new File("images/studentexample.pdf");
 try
 {
   // Lets open an image file
   Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Call Read]");
   myExampleTwo.setImageFile(mainCourse.readImageOldWay(file));
   Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Property set]");
 }
 catch (IOException ex)
 {
   Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
 }
 fatController.create(myExampleTwo);
 Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
````

You can easily prove that the JPA objects were persisted to the database by reading the objects back from the database.

````java
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
       mainCourse.writeFile(outfile, images.get(i).getImageFile());
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
       mainCourse.writeFile(outfile, images.get(i).getImageFile());
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
````

If you are wondering what code I used to write it to a file, it was simple like this.

````java
public void writeFile(File file, byte[] data) throws IOException
 {
   OutputStream fo = new FileOutputStream(file);
   // Write the data
   fo.write(data);
   // flush the file (down the toilet)
   fo.flush();
   // Close the door to keep the smell in.
   fo.close();
 }
````

Of course, if you were to use this in production you would add more error checking, and probably follow some better best practices. I have also skipped a lot of steps, assuming you know what you are doing 🙂

### 26th July 2012 – Update ###

So it has been a very long time since I wrote this and a comment was posted about people being lazy with the source.  At the time, I wrote there  was no easy way to share code.  Fast forward many years and google drive has come along..


For those of you whom like GIT I have also put it in GitHub at https://github.com/bovinemagnet/JPAandEJB-JPAImageExample


### 31st Janurary 2022 - Update ###

A few alerts from gitHubs depend-a-bot, so I figured I would update the code to a more recent version.