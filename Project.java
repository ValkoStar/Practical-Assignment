import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
  
public class Project 
{
   public static ArrayList<String> namesArray = new ArrayList<>();
   public static ArrayList<Long> totalSalesArray = new ArrayList<>();
   public static ArrayList<Long> salesPeriodArray = new ArrayList<>();
   public static ArrayList<Double> experienceMultiplierArray = new ArrayList<>();

    public static void main(String[] args) throws Exception 
    {

           JSONParser parser = new JSONParser();

           //Reading arguments from command line
           Object obj = parser.parse(new FileReader(new File(args[0])));
           
           boolean j = false;
           //Doing the following allows the system to handle the data and report file accordingly
           //No matter if the report.json file is first or second argument
           //The try method tries to make an array of object, if the report.json file is first argument,
           //An error will occur, since we have not enough objects in the file to make an array
           try{
               JSONArray jsonObjects =  (JSONArray) obj;
               j = true;
           }
           catch (Exception e) {
           }

           if (j)
           {
              data(args[0]);
              report(args[1]);
           }
           else{
              data(args[1]);
              report(args[0]);

           } 
       
   }

   public static void data(String filename)
   {
      JSONParser parser = new JSONParser();
      try{

      //Reading the data.json file with filename being the argument passed:
      Object obj = parser.parse(new FileReader(new File(filename)));
      
      JSONArray jsonObjects =  (JSONArray) obj;
      
      //Invoking all the names, totalsales, salesperiod and ex.Multiplier from the file 
      //And saving them in the corresponding array
      for (Object o : jsonObjects) {
              JSONObject jsonObject = (JSONObject) o;

               String name = (String) jsonObject.get("name");
               namesArray.add(name);

               Long totalSales = (Long)jsonObject.get("totalSales");
               totalSalesArray.add(totalSales);
   
               Long salesPeriod = (Long) jsonObject.get("salesPeriod");
               salesPeriodArray.add(salesPeriod);
   
               Double exp = (Double) jsonObject.get("experienceMultiplier");
               experienceMultiplierArray.add(exp);
           }
         } catch (FileNotFoundException e) {
               e.printStackTrace();
         } catch (IOException e) {
               e.printStackTrace();
         } catch (ParseException e) {
               e.printStackTrace();
         }

   }

   public static void report(String filename)
   {
      JSONParser parser = new JSONParser();
         try{

            //Getting the information needed from the file with filename being the second argument:
            Object obj = parser.parse(new FileReader(new File(filename)));

            JSONObject jsonObject = (JSONObject) obj;

            Long topPerfoTres = (Long) jsonObject.get("topPerformersThreshold");
   
            Boolean useXMulti = (Boolean)jsonObject.get("useExperienceMultiplier");
   
            Long periodLimit = (Long) jsonObject.get("periodLimit");

            calculate(topPerfoTres, useXMulti, periodLimit);
           
         } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
   

   }

   public static void calculate(Long topPerfoTres, Boolean useXMulti, Long periodLimit)
   {
      ArrayList<Double> scOrdered = new ArrayList<>();
      ArrayList<Double> scoreArray = new ArrayList<>();
      double score;

      //Calculating the scores/number of people/ that needs to be shown in the CSV file
      //Using the (x/100)*y /x% out of y/ formula
      double w = topPerfoTres.intValue();
      double k = namesArray.size();
      Double l = (w/100)*k;
      int scoresShown = l.intValue();

      //A loop to calculate the score of each of the employees and save them in an array
      for(int i = 0; i < namesArray.size(); i++)
      {
         if(useXMulti)
         {
             score = totalSalesArray.get(i)/salesPeriodArray.get(i)*experienceMultiplierArray.get(i);
         }
         else{
            score = totalSalesArray.get(i)/salesPeriodArray.get(i);
         }
         
         scOrdered.add(score);
         scoreArray.add(score);
      }

      //Sorting the array from the largest number to the smallest:
      Collections.sort(scOrdered, Collections.reverseOrder());

      //Saving in a new array only the scores needed to be shown /the largest/
      ArrayList<Double> topScores = new ArrayList<>();
      for(int b = 0; b < scoresShown; b++)
      {
         topScores.add(scOrdered.get(b));      
      }

      //An array containing the indexes of the employees with the highest scores
      ArrayList<Integer> indexesOfTopEmpl = new ArrayList<>();

      //Traversing through the arrays to find the index of the employee with a score needed to be shown
      for(int j = 0; j < topScores.size(); j++)
      {
         for(int b = 0; b < scoreArray.size(); b++)
         {
            //Comparing the top scores with all of the scores, when finding a match
            //saving the index in the array
            if(Double.compare(scoreArray.get(b), topScores.get(j)) ==0 && salesPeriodArray.get(b) <= periodLimit)
            {
               indexesOfTopEmpl.add(b);
            }
         }
         
      }

      //Making a temporary array removing the duplicate indexes:
      ArrayList<Integer> ar = new ArrayList<>();
      for (Integer element : indexesOfTopEmpl) {
  
         if (!ar.contains(element)) {

             ar.add(element);
         }
      }

      //A final check to be sure the code won't be displaying more employees in the CSV file than needed:
      int g = ar.size()-1;
      if(ar.size() > scoresShown)
      {
         //If the final array contains more scores, a removing process is started 
         //where the final elements will be removed:
         do{
            ar.remove(g);
            g--;
   
         }while(ar.size()>scoresShown);

      }
      
      //Calling the final method where the CSV file will be created, passing the array with the indexes and the scores array:
      createCSV(ar, scoreArray);
      
   }

   public static void createCSV(ArrayList<Integer> indexes, ArrayList<Double> scoresArray)
   {

      try{

         //Creating the CSV file and adding first arguments:
         FileWriter csvWriter = new FileWriter("names and scores.csv");
         csvWriter.append("Name");
         csvWriter.append(", ");
         csvWriter.append("Score");
         csvWriter.append("\n");
      
      //Adding all the names and scores:
      for(int i = 0; i< indexes.size(); i++)
      {
         //Adding the name and score correspoding to the index saved in the array:
         csvWriter.append(namesArray.get(indexes.get(i)));
         csvWriter.append(", ");
         csvWriter.append(scoresArray.get(indexes.get(i)).toString());
         csvWriter.append("\n");
      }
      csvWriter.flush();
      csvWriter.close();
      }catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }

   }

}

