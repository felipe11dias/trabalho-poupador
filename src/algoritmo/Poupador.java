package algoritmo;

import java.awt.*;
import java.util.*;

public class Poupador extends ProgramaPoupador {

	public int[][] mapa = new int[30][30];
	public int[][] translatorXY = {{-2, -2}, {-1, -2}, {0, -2}, {+1, -2}, {+2, -2}, {-2, -1}, {-1, -1}, {0, -1}, {+1, -1}, {+2, -2}, {-2, 0}, {-1, 0}, {+1, 0}, {+2, 0}, {-2, +1}, {-1, +1}, {0, +1}, {+1, +1}, {+2, +2}, {-2, +2}, {-1, +2}, {0, +2}, {+1, +2}, {+2, +2}};
	public boolean mapasetado = false;
	public Stack q = new Stack();
	public Node aux;
	public ArrayList<Node> visited = new ArrayList<Node>();


	public void printVision() {
		int size = sensor.getVisaoIdentificacao().length;
		int[] vision = sensor.getVisaoIdentificacao();
		System.out.println("-------------");

		Point position = sensor.getPosicao();
		//System.out.println(position.getX() + " " + position.getY());
		for (int i = 0; i < size; i++) {

			if (vision[i] >= 0) {
				System.out.print(" " + vision[i]);

			} else {
				System.out.print(vision[i]);

			}
			if (i == 4 || i == 9 || i == 13 || i == 18 || i == 23) {
				System.out.println();
			}
			if (i == 11) {
				System.out.print("  ");

			}
		}
		System.out.println("-------------");

	}

	//
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

		//System.out.println("Atual:"+sensor.getPosicao());
		for (String i : vision.keySet()) {
			if (vision.get(i)[0] == 0 || vision.get(i)[0] == 4 || vision.get(i)[0] == 3 || vision.get(i)[0] == 5 ) {
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
			Node visitado = visited.get(x);
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

	public int acao() {
		if (!mapasetado) {
			setMap();
			init();
			mapasetado = true;

		}
		int[][] positionOnMap = translationPosArrayToMatrix(sensor.getPosicao());
		writeToMap(sensor.getVisaoIdentificacao(), positionOnMap);
		int direction = exploreTheUnkown();
		//System.out.println(direction);
		return direction;
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


//public int acao() {
//	return (int) (Math.random() * 5);
//}
