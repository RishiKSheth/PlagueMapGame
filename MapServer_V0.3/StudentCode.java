import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class StudentCode extends Server{

    private String country1, country2;  // Countries selected from Box
    private ArrayList<String> p1ClickedList, p2ClickedList; // Keep track of clicked countries for each player
    private boolean player1Turn;
    private AtomicLong totalWorldPopulation;
    public static Long sendTot;


    // Initialize a single GraphAdjSet instance to be used throughout
    private GraphAdjSet graph;

    public StudentCode(){
        p1ClickedList = new ArrayList<String>();
        p2ClickedList = new ArrayList<String>();
        player1Turn = true;
        // Initialize the GraphAdjSet and load data only once
        graph = new GraphAdjSet();
        graph.readThroughBorders();  // Read borders and population from the CSV file
    	totalWorldPopulation = new AtomicLong(graph.returnTotalWorldPopulation());
    	sendTot = 0L;
    }

    @Override
    public void getInputCountries(String country1, String country2) {
        this.country1 = country1;
        this.country2 = country2;
        // Note #ee33ff is a HEX representation of RGB -> (238,51,255)
        addCountryColor(country1, "red");
        addCountryColor(country2, "#ee33ff");
        getColorPath();
    }

    @Override
    public void getColorPath() {
        if (country1 == null || country2 == null) return;

        // Using the already initialized 'graph' object to get neighbors and colors
        Set<String> borders = graph.getBorders(country1);
        System.out.println("BORDERS for " + country1 + ": ");
        for (String border : borders) {
            System.out.println(border);  // Print the neighboring countries
            addCountryColor(border, "yellow");
        }
    }

    @Override
    public void handleClick(String country) {
        // Add the clicked country to the appropriate player's list
        if (player1Turn){
            p1ClickedList.add(country);
        }
        else{ // Player 2
            p2ClickedList.add(country);
        }

        // Add color for the clicked country itself
        for (String c : p1ClickedList)
            addCountryColor(c, "green");
        for (String c : p2ClickedList)
            addCountryColor(c, "orange");

        // Print and color borders for the selected country
        //System.out.println(graph.getPopulationForCountry(country));
        ExecutorService e = Executors.newFixedThreadPool(10);
        CallableClass countryClicked = new CallableClass(country, true, graph, totalWorldPopulation);
        e.submit(countryClicked);

        Set<String> borders = graph.getBorders(country);
        System.out.println("Country Population for " + country);
        System.out.println(graph.getCountryPopulation(country));
        System.out.println("BORDERS for " + country + ": ");
        for (String border : borders) {
		    System.out.println("sendtot: " + sendTot);
            System.out.println(border);  // Print the neighboring countries
            addCountryColor(border, "red");
            System.out.println("Country Population for "+border+ ": ");
            System.out.println("sendtot: " + sendTot);
            System.out.println(graph.getCountryPopulation(border));
			CallableClass bordersClicked = new CallableClass(border, false, graph, totalWorldPopulation);
			e.submit(bordersClicked);
		    System.out.println("sendtot: " + sendTot);

            //System.out.println(graph.getPopulationForCountry(border));

			//prints out and gets TOTAL world population
            //System.out.println(graph.returnTotalWorldPopulation());
        }

        // Toggle the player's turn
        player1Turn = !player1Turn;
    }

    public Long returnSendTot(){
		return sendTot;
	}

    public String getCountry1() { return country1; }
    public String getCountry2() { return country2; }

	public class CallableClass implements Callable<Long>{
		private String country;
		private boolean isClickedCountry;
		private GraphAdjSet graph;
		private AtomicLong worldPop;

		public CallableClass(String country, boolean isClickedCountry, GraphAdjSet graph, AtomicLong worldPop){
			this.country = country;
			this.isClickedCountry = isClickedCountry;
			this.graph = graph;
			this.worldPop = worldPop;

		}
		@Override
		public Long call() throws Exception{
			 long initialPopulation = graph.getCountryPopulation(country);
			    long currentPopulation = initialPopulation;

			    // Rate and duration
			    long reductionPerStep = isClickedCountry ? 2000000L : 500000L;
			    long sleepDuration = 300;

			    System.out.println("Reducing population for " + country +
			        " starting at " + initialPopulation + ", rate: -" + reductionPerStep + " per " + sleepDuration + "ms");

			    while (currentPopulation > 0) {
			        try {
			            Thread.sleep(sleepDuration);
			        } catch (InterruptedException e) {
			            System.out.println("Thread interrupted for " + country);
			            break;
			        }

			        long reduction = Math.min(reductionPerStep, currentPopulation);
			        currentPopulation -= reduction;
			        worldPop.addAndGet(-reduction);
			        System.out.println("Population of the world is: " + worldPop.get());
			        sendTot = worldPop.get();
			    }

    		return currentPopulation;
		}

		public AtomicLong getWorldPop(){
			return totalWorldPopulation;
		}
	}

    public static void main(String[] args) {
        Server server = new StudentCode(); // Initialize server on default port (8000)
        server.run(); // Start the server
        server.openURL(); // Open url in browser
    }
}