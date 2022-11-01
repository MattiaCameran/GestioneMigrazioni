package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {

	//Ricordiamoci di pensare ai quattro tipi di dati coinvolti:
	
	//Coda degli eventi
	
	//L'evento principale da modellare è l'ingresso di persone in uno stato. Devo calcolare il numero di persone stanziali, quelle che devono spostarsi, dove e in che modo.
	//Ci serve sapere: tempo, stato, persone in uscita verso quello stato, quanti sono gli stati confinanti e quali sono. Creo la classe Event.
	//Questo elemento può essere il nostro elemento della coda.
	private PriorityQueue<Event> queue;
	
	//Parametri di simulazione
	private int nInizialeMigranti;			//Numero iniziale di migranti
	private Country nazioneIniziale;		//Stato iniziale dello stato di partenza
	
	
	//Output della simulazione
	private int nPassi;						//Numero di passi della simulazione.
	private Map<Country, Integer> persone;	//Per ogni nazione, quanti migranti sono stanziali in essa alla fine della simulazione.
											//Potrei anche usare una lista di CountryAndNumber ma sarebbe meno efficiente per le modifiche.
	
	//Stato del mondo simulato
	private Graph<Country, DefaultEdge> grafo;	//Il grafo appartiene allo stato del mondo, non è qualcosa di modificabile dall'utente.
	
	//Cosa caratterizza lo stato del mondo in ciascun istante di tempo? Quante persone ci sono in ogni stato-> La mappa persone ci servirà.
	
	
	
	//Ho scritto le tipologie di dati, ora mi servono i metodi (Costruttore e altri).
	
	//Nel costruttore, ovvero all'inizio del programma, io passo solo il grafo. Il resto lo inizializzo in altri metodi.
	public Simulatore(Graph<Country, DefaultEdge> grafo) {
			this.grafo = grafo;
	}
	
	//Questo metodo corrisponde all'inizializzazione della simulazione. Qua io inserisco da quale stato parto e con quanti migranti inizio. Inoltre qua inizializzo e riempio la
	//Mappa che mi dice quanti migranti si trovano in ogni stato.
	//Fare tutto questo qui mi permette di fare in modo che facendo nuove simulazioni con nuovi dati tramite il richiamo di questo metodo i dati vecchi vengano sovrascritti.
	public void initialize(Country partenza, int migranti) {
		this.nazioneIniziale = partenza;
		this.nInizialeMigranti = migranti;
		
		this.persone = new HashMap<Country, Integer>();
	
		for(Country c: this.grafo.vertexSet()) {
			this.persone.put(c, 0);		//Inizializzo la mappa mettendo a 0 il numero di migranti per ogni stato confinante al mio country.
		}
		
		this.queue = new PriorityQueue<>();	//Ricordarsi di inizializzare la coda.
		
		this.queue.add(new Event(1, this.nazioneIniziale, this.nInizialeMigranti));	//Io dopo che ho inizializzato le variabili devo anche inserire nella coda il primo evento, altrimenti la simulazione si ferma prima di partire (coda vuota).
		//Subito io inserisco quindi un evento al tempo 1, dove inserisco la nazione iniziale con il numero iniziale di migranti.
		
	}
	
	//Ora creo il metodo di simulazione vera e propria.
	public void run() {
		
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}	//Questo metodo mi estrae l'evento dalla coda e lo elabora. Come lo elabora però? Devo dirglielo, creando un nuovo metodo.
	}

	private void processEvent(Event e) {
		
		int stanziali = e.getPersone()/2;			//Nello stato rimangono il 50% delle persone. Questi diventano subito stanziali. Ricorda: la divisione arrotonda per difetto.
		int migranti = e.getPersone() - stanziali;	//Questi invece sono quelli che dovrebbero spostarsi.
		
		//Ora mi serve sapere quanti stati confinanti ho. Lo ricavo dal grafo.
		int confinanti = this.grafo.degreeOf(e.getNazione());	//Il metodo degreeOf() mi permette, passato un vertice di un grafo come parametro, di ottenerne il numero di confinanti.
		
		int gruppiMigranti = migranti/confinanti;		//Gruppi di migranti per ogni stato al livello corrente.
		
		stanziali = stanziali + migranti % confinanti;	//Se la divisione generava un resto, questi rimangono stanziali.
		
		//Ora abbiamo tutto per aggiornare il numero di stanziali in ogni singolo stato.
		//Aggiorno lo stato del mondo
		this.persone.put(e.getNazione(), this.persone.get(e.getNazione())+ stanziali);	//Aggiorno nella mappa che tiene traccia del numero di migranti per ogni stazione il numero di stanziali
	
		this.nPassi = e.getTime();	//Il numero passi sarà uguale al tempo dell'evento siccome io parto da 1.
		
		//Ora devo aggiornare gli eventi. Devo farmi dire dal grafo quali sono gli stati confinanti andando a prendere tutti i vertici adiacenti ad un vertice dato.
		//Predispongo gli eventi futuri (solamente se gruppi migranti è diverso da zero ovviamente)
		if(gruppiMigranti!=0) {
		for (Country vicino: Graphs.neighborListOf(this.grafo, e.getNazione())) {	//Per ogni paese confinante al vertice di partenza.
			
			this.queue.add(new Event(e.getTime()+1, vicino, gruppiMigranti));		//Aggiungo un nuovo evento dove il tempo sarà aumentato di 1, il paese sarà "vicino" (creo un evento per ogni "vicino") e il numero migranti sarà dato da gruppiMigranti, ovvero la percentuale di migranti in gruppo che si è spostata in ogni paese confinante.
		}
		}
			
	}

	public int getnPassi() {
		return nPassi;
	}

	public Map<Country, Integer> getPersone() {
		return persone;
	}
}
