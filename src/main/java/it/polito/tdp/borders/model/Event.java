package it.polito.tdp.borders.model;

public class Event implements Comparable<Event>{

	private int time;			//Variabile tempo.
	private Country nazione;	//La nazione in cui mi trovo.
	private int persone;		//Il numero di persone che sono nella nazione.
	
	
	public Event(int time, Country nazione, int persone) {
		this.time = time;
		this.nazione = nazione;
		this.persone = persone;
	}

	public int getTime() {
		return time;
	}

	public Country getNazione() {
		return nazione;
	}

	public int getPersone() {
		return persone;
	}

	@Override
	public int compareTo(Event other) {	
		return this.time-other.time;		//Come ogni evento, va ordinato per tempo.
	}
}
