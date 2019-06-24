package it.polito.tdp.gestionale.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.gestionale.db.DidatticaDAO;

public class Model {

	private List<Corso> corsi;
	private List<Studente> studenti;
	private DidatticaDAO dao;
	private SimpleGraph<Nodo, DefaultEdge> grafo;
	private Map<Integer, Studente> mapStudenti;
	private List<Nodo> vertici;
	private ArrayList<Corso> ottima;

	public Model() {
		dao= new DidatticaDAO();
		mapStudenti= new HashMap<Integer, Studente>();
		
		
		
	}
	public String creaGrafo() {
		corsi= new LinkedList<Corso>(dao.getTuttiICorsi());
		studenti= new LinkedList<Studente> (dao.getTuttiStudenti(mapStudenti));
		vertici= new LinkedList<Nodo>(corsi);
		vertici.addAll(studenti);
		String s="";
		int count;
		grafo= new SimpleGraph<Nodo, DefaultEdge>(DefaultEdge.class);
		Graphs.addAllVertices(grafo, vertici);
		
		for(Corso c: corsi) {
			dao.getStudentiIscrittiAlCorso(c, mapStudenti);
			for(Studente st: c.getStudenti()) {
				grafo.addEdge(c, st);
		}
			}
		System.out.println("Grafo con: "+grafo.vertexSet().size()+ " vertici e "+
			grafo.edgeSet().size()+" archi");
		for(int i=0; i<11; i++) {
			count=0;
			for(Studente tizio: studenti) {
				if(tizio.getCorsi().size()==i) {
					count++;
				}
			}
			s+= " "+count+" studenti frequentano "+i+" corsi\n";
			
		}
		return s;
	}
	
	public List<Corso> findMinimalSet(){
		List<Corso> soluzioneParziale = new ArrayList<Corso>();
		List<Corso> soluzioneMigliore = new ArrayList<Corso>();
		
		recursive(soluzioneParziale, soluzioneMigliore);
		
		return soluzioneMigliore;
	}
	
	private void recursive(List<Corso> soluzioneParziale, List<Corso> soluzioneMigliore) {
		
//		System.out.println(soluzioneParziale);
		
		HashSet<Studente> hashSetStudenti = new HashSet<Studente>(studenti);
		for (Corso corso : soluzioneParziale) {
			hashSetStudenti.removeAll(corso.getStudenti());
		}
		if (hashSetStudenti.isEmpty()) {
			if (soluzioneMigliore.isEmpty())
				soluzioneMigliore.addAll(soluzioneParziale);
			if (soluzioneParziale.size() < soluzioneMigliore.size()){
				soluzioneMigliore.clear();
				soluzioneMigliore.addAll(soluzioneParziale);
			}
		}
		
		for (Corso corso : corsi) {
			if (soluzioneParziale.isEmpty() || corso.compareTo(soluzioneParziale.get(soluzioneParziale.size()-1)) > 0) {
				soluzioneParziale.add(corso);
				recursive(soluzioneParziale, soluzioneMigliore);
				soluzioneParziale.remove(corso);
			}
		}
		
	}
	public List<Corso> getPercorsoMinimo(){
		this.ottima = new ArrayList<Corso>(corsi);
		List<Corso> parziale = new ArrayList<Corso>();
		
	
		cercaPercorso(parziale);
		
		return this.ottima;
	}

	private void cercaPercorso(List<Corso> parziale) {
		
		//vedere se la soluzione corrente è migliore della ottima corrente
		
		HashSet<Studente> tutti= new HashSet<Studente>(studenti);
		
		for(Corso corso: parziale) {
			tutti.removeAll(corso.getStudenti());
		}
		
		
		if(tutti.isEmpty()) {
			if(parziale.size() < ottima.size()) {
			this.ottima.clear();
			this.ottima.addAll(parziale);
		}}
		
		//ottengo tutti i candidati
		
		for(Corso candidato : corsi) {
			if(parziale.isEmpty() || candidato.compareTo(parziale.get(parziale.size()-1))>0) { //SENZA QUESTA CONDIZIONE 
				parziale.add(candidato);													   //SI IMPALLLAVA! È IMPORTANTE!
				this.cercaPercorso(parziale);
				parziale.remove(candidato);
			}
		}
		
		
		
		
	}
	
		
}
