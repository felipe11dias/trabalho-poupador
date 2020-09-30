package algoritmo;

import java.awt.*;
import java.util.*;
import java.util.List;


public class Ladrao extends ProgramaLadrao {
	private int[][] mapa = new int[30][30];
	public int[][] translatorXY = {{-2, -2}, {-1, -2}, {0, -2}, {+1, -2}, {+2, -2}, {-2, -1}, {-1, -1}, {0, -1}, {+1, -1}, {+2, -2}, {-2, 0}, {-1, 0}, {+1, 0}, {+2, 0}, {-2, +1}, {-1, +1}, {0, +1}, {+1, +1}, {+2, +2}, {-2, +2}, {-1, +2}, {0, +2}, {+1, +2}, {+2, +2}};

	public boolean mapa_check = false;
	public Stack q = new Stack();
	public Node aux;
	public ArrayList<Node> visited = new ArrayList<Node>();
	public ArrayList<Integer> poupadoresVisualizados = new ArrayList<Integer>();	
	public ArrayList<Integer> poupadoresRoubados = new ArrayList<Integer>();
	public HashMap<Integer, Integer> poupadoresRoubadosDistracao = new HashMap<Integer, Integer>();

	// Array que armazena valores onde o poupador foi detectado a esquedar do ladrao
	private int[] poupadorDectadoEsquerda = {5,6,10,11,14,15};
	
	// Array que armazena valores onde o poupador foi detectado a direita do ladrao
	private int[] poupadorDectadoDireita = {8,9,12,13,17,18};
		
	// Array que armazena valores onde o poupador foi detectado a cima do ladrao
	private int[] poupadorDectadoCima = {0,1,2,3,4,7};
	
	// Array que armazena valores onde o poupador foi detectado a baixo do ladrao
	private int[] poupadorDectadoBaixo = {19,20,21,22,23,16};
	
	public int acao() {
		if (!mapa_check) {
			setMap();
			init();
			mapa_check = true;
		}
		int[][] positionOnMap = translationPosArrayToMatrix(sensor.getPosicao());
		writeToMap(sensor.getVisaoIdentificacao(), positionOnMap);
		poupadoresVisualizados.clear();
		visualizarPoupadores();
		System.out.println("poupadoresRoubados: " + poupadoresRoubados );
		System.out.println("poupadoresRoubadosDistracao: " + poupadoresRoubadosDistracao );
		return agir();
	}
	
	private int previnirMovimentos(int posicaoPoupadorEncontrado) {
		// Previne movimentos
		int[] movimentos = {11,12,7,16};
		int[] movimentosPossiveis = new int[4];
		int count = 0;
		for(int i = 0; i < movimentos.length - 1; i++) {
			if(sensor.getVisaoIdentificacao()[movimentos[i]] == 0 || sensor.getVisaoIdentificacao()[movimentos[i]] >= 100 && sensor.getVisaoIdentificacao()[movimentos[i]] < 200) {
				movimentosPossiveis[count] = movimentos[i];
				count++;
			}
		}

		return previnir(movimentosPossiveis, posicaoPoupadorEncontrado);
	}
	// Vejo poupadores ao redor, se tiver
	private void visualizarPoupadores() {
		for(int i = 0; i < sensor.getVisaoIdentificacao().length; i++) {
			if(sensor.getVisaoIdentificacao()[i] >= 100 && sensor.getVisaoIdentificacao()[i] < 200 ) {
				poupadoresVisualizados.add(sensor.getVisaoIdentificacao()[i]);
			}
		}
	}
	// Age em perseguir um poupador evitando movimentos inueis

// OBS: previnirMovimentos tem que direcionar para melhor possição dentre as escolhidas
	private int agir() {
		// Persegue poupador
		if(existePoupadoresNaoRoubadosVisao(poupadoresRoubados, poupadoresVisualizados) != null) {
			for(int i = 0; i < sensor.getVisaoIdentificacao().length; i++) {
				if(sensor.getVisaoIdentificacao()[i] >= 100 && sensor.getVisaoIdentificacao()[i] < 200 ) {
					ArrayList<Integer> poupadoresNaoRoubados = existePoupadoresNaoRoubadosVisao(poupadoresRoubados, poupadoresVisualizados);
					if(poupadoresNaoRoubados.contains(sensor.getVisaoIdentificacao()[i]) && (poupadorEncontrado(poupadorDectadoEsquerda, i) || sensor.getVisaoIdentificacao()[11] >= 100 && sensor.getVisaoIdentificacao()[11] < 200)) {
						if(sensor.getVisaoIdentificacao()[11] >= 1 && sensor.getVisaoIdentificacao()[11] < 6) {
							return previnirMovimentos(i);
						}else if(sensor.getVisaoIdentificacao()[11] >= 100 && sensor.getVisaoIdentificacao()[11] < 200 && !poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[11]) && !poupadoresRoubadosDistracao.containsKey(sensor.getVisaoIdentificacao()[11])) {
							poupadoresRoubados.add(sensor.getVisaoIdentificacao()[11]);
							poupadoresRoubadosDistracao.put(sensor.getVisaoIdentificacao()[11], 10);
							return 4;
						}
						if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[i])) {		
							if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1 == 0)) {
								poupadoresRoubados.remove((Integer)sensor.getVisaoIdentificacao()[i]);
								poupadoresRoubadosDistracao.remove(sensor.getVisaoIdentificacao()[i]);
							}else if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null){
								poupadoresRoubadosDistracao.replace(sensor.getVisaoIdentificacao()[i], (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1));						
							}
						}						
						return 4;
					}
					
					if(poupadorEncontrado(poupadorDectadoDireita, i) || sensor.getVisaoIdentificacao()[12] >= 100 && sensor.getVisaoIdentificacao()[12] < 200) {
						if(sensor.getVisaoIdentificacao()[12] >= 1 && sensor.getVisaoIdentificacao()[12] < 6) {
							return previnirMovimentos(i);
						}else if(sensor.getVisaoIdentificacao()[12] >= 100 && sensor.getVisaoIdentificacao()[12] < 200 && !poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[12]) && !poupadoresRoubadosDistracao.containsKey(sensor.getVisaoIdentificacao()[11])) {
							poupadoresRoubados.add(sensor.getVisaoIdentificacao()[12]);
							poupadoresRoubadosDistracao.put(sensor.getVisaoIdentificacao()[12], 10);
							return 3;
						}
						if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[i])) {		
							if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1 == 0)) {
								poupadoresRoubados.remove((Integer)sensor.getVisaoIdentificacao()[i]);
								poupadoresRoubadosDistracao.remove(sensor.getVisaoIdentificacao()[i]);
							}else if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null){
								poupadoresRoubadosDistracao.replace(sensor.getVisaoIdentificacao()[i], (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1));						
							}
						}
						return 3;
					}
					
					if(poupadorEncontrado(poupadorDectadoBaixo, i) || sensor.getVisaoIdentificacao()[16] >= 100 && sensor.getVisaoIdentificacao()[16] < 200 ) {
						if(sensor.getVisaoIdentificacao()[16] >= 1 && sensor.getVisaoIdentificacao()[16] < 6) {
							return previnirMovimentos(i);
						}else if(sensor.getVisaoIdentificacao()[16] >= 100 && sensor.getVisaoIdentificacao()[16] < 200 && !poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[16]) && !poupadoresRoubadosDistracao.containsKey(sensor.getVisaoIdentificacao()[11])) {
							poupadoresRoubados.add(sensor.getVisaoIdentificacao()[16]);
							poupadoresRoubadosDistracao.put(sensor.getVisaoIdentificacao()[16], 10);
							return 2;
						}
						if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[i])) {
							if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1 == 0) {
								poupadoresRoubados.remove((Integer)sensor.getVisaoIdentificacao()[i]);
								poupadoresRoubadosDistracao.remove(sensor.getVisaoIdentificacao()[i]);
							}else if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null){
								poupadoresRoubadosDistracao.replace(sensor.getVisaoIdentificacao()[i], (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1));						
							}						
						}
						return 2;
					}
					
					if(poupadorEncontrado(poupadorDectadoCima, i) || sensor.getVisaoIdentificacao()[7] >= 100 && sensor.getVisaoIdentificacao()[7] < 200) {
						if(sensor.getVisaoIdentificacao()[7] >= 1 && sensor.getVisaoIdentificacao()[7] < 6) {
							return previnirMovimentos(i);
						}else if(sensor.getVisaoIdentificacao()[7] >= 100 && sensor.getVisaoIdentificacao()[7] < 200 && !poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[7]) && !poupadoresRoubadosDistracao.containsKey(sensor.getVisaoIdentificacao()[11])) {
							poupadoresRoubados.add(sensor.getVisaoIdentificacao()[7]);
							poupadoresRoubadosDistracao.put(sensor.getVisaoIdentificacao()[7], 10);
							return 1;
						}
						if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[i])) {		
							if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1 == 0)) {
								poupadoresRoubados.remove((Integer)sensor.getVisaoIdentificacao()[i]);
								poupadoresRoubadosDistracao.remove(sensor.getVisaoIdentificacao()[i]);
							}else if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null){
								poupadoresRoubadosDistracao.replace(sensor.getVisaoIdentificacao()[i], (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1));						
							}
						}
						return 1;
					}
					
					if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && poupadoresRoubados.contains(sensor.getVisaoIdentificacao()[i])) {		
						if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null && (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1 == 0)) {
							poupadoresRoubados.remove((Integer)sensor.getVisaoIdentificacao()[i]);
							poupadoresRoubadosDistracao.remove(sensor.getVisaoIdentificacao()[i]);
						}else if(poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) != null){
							poupadoresRoubadosDistracao.replace(sensor.getVisaoIdentificacao()[i], (poupadoresRoubadosDistracao.get(sensor.getVisaoIdentificacao()[i]) - 1));						
						}
					}
				}	
			}
		}
		return exploreTheUnkown();
	}
	
	// Verifica se existePoupadoresNaoRoubados ao redor
	private ArrayList<Integer> existePoupadoresNaoRoubadosVisao(ArrayList<Integer> poupadoresRoubados2, ArrayList<Integer> poupadoresVisualizados2) {
		ArrayList<Integer> poupadoresNaoRoubados = new ArrayList<Integer>();
		if(poupadoresVisualizados2.isEmpty()) {
			return null;
		}
		if(poupadoresRoubados2.isEmpty()) {
			for(int j = 0; j < poupadoresVisualizados2.size(); j++) {
					poupadoresNaoRoubados.add(poupadoresVisualizados2.get(j));
			}
		}else {
			for(int i = 0; i < poupadoresRoubados2.size(); i++) {
				for(int j = 0; j < poupadoresVisualizados2.size(); j++) {
					if(!poupadoresRoubados2.contains(poupadoresVisualizados2.get(j))) {
						poupadoresNaoRoubados.add(poupadoresVisualizados2.get(j));
					}
				}
			}
		}
		return poupadoresNaoRoubados;	
	}
	
	//Faz um  random dos valores de uma array, especie de random customizado para um conjunto de valores escolhido pelo o usuario
	private int previnir(int[] movimentosPossiveis, int posicaoPoupadorEncontrado) {
		if(movimentosPossiveis.length != 0) {
			if(poupadorEncontrado(poupadorDectadoEsquerda, posicaoPoupadorEncontrado)) {
				if(movimentoPossivel(movimentosPossiveis, 7)) {
					return 1;
				}else if(movimentoPossivel(movimentosPossiveis, 16)) {
					return 2;
				}else {
					return 3;
				}
			} 
		}
		if(movimentosPossiveis.length != 0) {
			if(poupadorEncontrado(poupadorDectadoDireita, posicaoPoupadorEncontrado)) {
				if(movimentoPossivel(movimentosPossiveis, 7)) {
					return 1;
				}else if(movimentoPossivel(movimentosPossiveis, 16)) {
					return 2;
				}else {
					return 4;
				}
			} 
		}
		if(movimentosPossiveis.length != 0) {
			if(poupadorEncontrado(poupadorDectadoCima, posicaoPoupadorEncontrado)) {
				if(movimentoPossivel(movimentosPossiveis, 11)) {
					return 4;
				}else if(movimentoPossivel(movimentosPossiveis, 12)) {
					return 3;
				}else {
					return 2;
				}
			} 
		}
		if(movimentosPossiveis.length != 0) {
			if(poupadorEncontrado(poupadorDectadoBaixo, posicaoPoupadorEncontrado)) {
				if(movimentoPossivel(movimentosPossiveis, 11)) {
					return 4;
				}else if(movimentoPossivel(movimentosPossiveis, 12)) {
					return 3;
				}else {
					return 1;
				}
			} 
		}
		return exploreTheUnkown(); 
	}
	// Verifica se o movimento possivel existe dentro do conjunto de movimentos possiveis.
	public boolean movimentoPossivel(int[] vetor, int movimento) {
		for(int i = 0; i <= vetor.length - 1; i++) {
			if(vetor[i] == movimento) {
				return true;
			}
		}
		return false;
	}
	// Verifica um poupador localizado a partir de uma direção e o id do poupador.
	public boolean poupadorEncontrado(int[] vetor, int poupador) {
		for(int i = 0; i <= vetor.length - 1; i++) {
			if(vetor[i] == poupador) {
				return true;
			}
		}
		return false;
	}
	public void setMap() {
		for (int i = 0; i < 30; i++) {
			for (int x = 0; x < 30; x++) {
				mapa[i][x] = -5;

			}
		}

	}
	public int[][] translationPosArrayToMatrix(Point point) {
		int x = point.x;
		int y = point.y;

		int size = translatorXY.length;

		int[][] positionOnMap = new int[size][2];

		for (int i = 0; i < size; i++) {
			positionOnMap[i][0] = translatorXY[i][0] + x;
			positionOnMap[i][1] = translatorXY[i][1] + y;
		}

		return positionOnMap;

	}
	public void printMap() {
		System.out.println("--------------------------------------------------------------------------------------");
		for (int i = 0; i < 30; i++) {
			for (int x = 0; x < 30; x++) {
				if (mapa[i][x] < 0) {
					//System.out.print("" + mapa[i][x]);
					System.out.print("(" + i + " " + x + ")" + "" + mapa[i][x]);

				} else {
					//System.out.print(" " + mapa[i][x]);
					System.out.print("(" + i + " " + x + ")" + " " + mapa[i][x]);

				}
			}
			System.out.println();

		}
		System.out.println("--------------------------------------------------------------------------------------");

	}

	public void writeToMap(int[] vision, int[][] positionOnMap) {
		//for (int i = 0; i < vision.length; i++) {
		//	System.out.print(" "+vision[i]);
		//}
		for (int i = 0; i < vision.length; i++) {
			int x = positionOnMap[i][0];
			int y = positionOnMap[i][1];
			//System.out.println( x+ " " +y);
			if (x >= 0 && y >= 0 && x < 30 && y < 30 && vision[i] != -2) {
				mapa[y][x] = vision[i];
				//System.out.println( x+ " " +y + " " + vision[i]);

			}
		}


	}

	public int getValueFromMap(Point ponto) {
		return mapa[ponto.y][ponto.x];
	}

	public int exploreTheUnkown() {
		if(q.empty()){
			aux=null;
			init();
			visited.clear();
			System.out.println("resetando");
			System.gc();
		}
		aux = (Node) q.pop();
		// if(visited.size()>5){
		//     visited.clear();
		// }

		HashMap<String, Integer[]> vision = new HashMap<String, Integer[]>();
		int[][] positionOnMap = translationPosArrayToMatrix(sensor.getPosicao());
		int esquerda = sensor.getVisaoIdentificacao()[11];
		int direta = sensor.getVisaoIdentificacao()[12];
		int cima = sensor.getVisaoIdentificacao()[7];
		int baixo = sensor.getVisaoIdentificacao()[16];
		vision.put("esquerda", new Integer[]{esquerda, positionOnMap[11][0], positionOnMap[11][1], 4});
		vision.put("direta", new Integer[]{direta, positionOnMap[12][0], positionOnMap[12][1], 3});
		vision.put("cima", new Integer[]{cima, positionOnMap[7][0], positionOnMap[7][1], 1});
		vision.put("baixo", new Integer[]{baixo, positionOnMap[16][0], positionOnMap[16][1], 2});
		List<String> visionKeys = new ArrayList<String>(vision.keySet());
		Collections.shuffle(visionKeys);
		//System.out.println("Atual:"+sensor.getPosicao());
		for (Object i : visionKeys) {
			if (vision.get(i)[0] == 0) {
				//System.out.println(i + " " + vision.get(i)[0] + " " + vision.get(i)[1] + " " + vision.get(i)[2]);
				Point destino = new Point(vision.get(i)[1], vision.get(i)[2]);
				if (!vizinhosVisitados(destino)) {
					if (vision.get(i)[1] != aux.pai.id.x || vision.get(i)[2] != aux.pai.id.y) {
						aux.pai.pai=null;
						int value = getValueFromMap(destino);
						Node new_node = new Node(destino, value, vision.get(i)[3]);
						new_node.setPai(aux);
						q.add(new_node);
						visited.add(aux);
						//System.out.println("entrou " + vision.get(i)[3]);

					}
				}

			}
		}
		//printNode(aux);
		int direction = aux.direction;
		aux = null;
		return direction;
	}

	public void printNode(Node aux){
		System.out.println("aux:"+aux);
		System.out.println("aux_direction:"+aux.direction);
		System.out.println("aux_id:"+aux.id);
		System.out.println("q:"+q);
	}

	public boolean vizinhosVisitados(Point vizinho) {
		for (int x = 0; x < visited.size(); x++) {
			Node visitado = (Node) visited.get(x);
			if (visitado.id.x == vizinho.x && visitado.id.y == vizinho.y) {
				return true;
			}
		}
		return false;
	}

	public void init() {

		Node node = new Node(sensor.getPosicao(), getValueFromMap(sensor.getPosicao()), 0);
		node.setPai(new Node(new Point(-9, -9), -9, 0));
		q.add(node);
		aux = node;


	}

	private class Node {
		Point id;
		int value;
		Node pai;
		int direction;

		@Override
		public String toString() {
			return "Node{" +
					"id=" + id +
					", value=" + value +
					", pai=" + pai +
					", direction=" + direction +
					'}';
		}

		public Node(Point id, int value, int direction) {
			this.id = id;
			this.value = value;
			this.direction = direction;
		}

		public void setPai(Node pai) {
			this.pai = pai;
		}
	}



}