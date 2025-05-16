public class countryByPopulationAndName{
	private String countryName;
	private int countryPopulation;

	public countryByPopulationAndName(String countryName, int countryPopulation){
		this.countryName = countryName;
		this.countryPopulation = countryPopulation;
	}

	public String getCountryName(){
		return countryName;
	}

	public int getCountryPopulation(){
		return countryPopulation;
	}

	public void setCountryPopulation(int newPopulation){
		countryPopulation = newPopulation;
	}

	public void setCountryName(String newCountryName){
		countryName = newCountryName;
	}
}