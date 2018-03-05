import java.util.*;
import java.io.*;


class Matriz implements Comparable<Matriz>
{
    char movimento_anterior; //movimento que foi feito para chegar a esta config.
    int profundidade, heuristica;
    int [][] matriz = new int [4][4];
    int linha0, coluna0; //coordenadas do espaço vazio
    Matriz pai;
    String key;

    Matriz(int [][] a, int profundidade, Matriz pai, char movimento_anterior, int linha0, int coluna0)
    {
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                this.matriz[i][j]=a[i][j];

        this.profundidade=profundidade;
        this.movimento_anterior=movimento_anterior;
        this.linha0=linha0;
        this.coluna0=coluna0;
        this.key=converterParaString(matriz);
        this.pai=pai;
    }


    public String converterParaString(int mat[][])
    {
    	
        String key = "";

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                int aux= 'A' + mat[i][j];
                char letter = (char)aux;
                key += letter;
                //System.out.println(key);
            }
        }
        return key;
    }


    @Override
    public int compareTo(Matriz n){
		if(this.heuristica > n.heuristica)
			return 1;
		else if(this.heuristica < n.heuristica)
			return-1;
        else
            return 0;

	}

}

public class Jogo15
{
    static Stack<Matriz> pilha=new Stack<Matriz>(); //para o DFS
    static Queue<Matriz> fila=new LinkedList<Matriz>(); //para o BFS
    static PriorityQueue<Matriz> heap = new PriorityQueue<Matriz>(); //a* e gulosa
    static int opcao=0;
    static String solucao=new String("");
    static Matriz mi;
    static Matriz fi;
    static int limite_iterativo=1,flag=0;
    public static void main(String Args[])
    {
        Scanner input = new Scanner(System.in);
        boolean solvability;
        int matriz_inicial[][] = new int[4][4];
        int matriz_final[][] = new int [4][4];
        
        while((opcao<1) || (opcao>5))
        {
        	System.out.println("Escolha o algoritmo a excutar:");
        	System.out.println("1-BFS");
        	System.out.println("2-DFS");
        	System.out.println("3-Iterativa em profundidade");
        	System.out.println("4-Estrela");
        	System.out.println("5-Guloso");
        	System.out.println();
        	opcao=input.nextInt();
        }
        
        System.out.println();
        System.out.println("Insira estado inicial do jogo: (16 inteiros entre 0-15");
        int l0=0,c0=0;
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                {
                    matriz_inicial[i][j]=input.nextInt();
                    if(matriz_inicial[i][j]==0)
                    {
                        l0=i;
                        c0=j;
                    }
                }
        System.out.println();
        System.out.println("Insira estado final do jogo: (16 inteiros entre 0-15");
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                {
                    matriz_final[i][j]=input.nextInt();
                }
        System.out.println();
                
        solvability = Solvabilidade(matriz_inicial, matriz_final);
        
        if(solvability == false)
        {
            System.out.println("Nao tem soluçao");
            System.exit(0);
        }
        
        mi = new Matriz(matriz_inicial,0,null,'x',l0,c0);
        fi = new Matriz(matriz_final,0,null,'x',0,0);

        double start = new Date().getTime();

        if(opcao==1)
        {
            System.out.println("A pesquisar por BFS ...");
            System.out.println();
            bfs();
        }

        if(opcao==2)
        {
            System.out.println("A pesquisar por DFS ... (Com profundidade limitada a 25)");
            System.out.println();
            dfs();
        }

        if(opcao==3)
        {
            System.out.println("A pesquisar por Iterativa em profundidade ...");
            System.out.println();
            while(flag==0)
            {
                dfs();
                limite_iterativo++;
            }
        }

        if(opcao==4 || opcao==5)
        {
            if(opcao==4) System.out.println("A pesquisar por A*");
            else System.out.println("A perquisar com o guloso");
            System.out.println();
            estrela();
        }

        double end = new Date().getTime();
        System.out.println("Tempo de execucao: " + ((end-start)/1000)+"s");
        double memoria = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.printf("Memoria utilizada: %.3fMB\n",memoria/(1024*1024));
    }



    //FUNÇÕES
    
    public static void bfs()
    {
        fila.add(mi);

        while(fila.peek()!=null)
        {
            if(fila.peek().key.equals(fi.key))
            {
                Matriz aux = fila.peek();
                System.out.println("Profundidade: " + aux.profundidade);
                while(aux.profundidade>0)
                {
                    //printMatriz(aux);
                    solucao=solucao+aux.movimento_anterior;
                    aux=aux.pai;
                }
                System.out.print("Solução: ");
                printCaminho(solucao);
                break;
            }

            else gerarFilhos(fila.poll());
        }
    }


    public static void dfs()
    {
        pilha.push(mi);

        while(!pilha.empty())
        {
            if(pilha.peek().key.equals(fi.key))
            {
                flag=1;
                Matriz aux = pilha.peek();
                System.out.println("Profundidade: " + aux.profundidade);

                while(aux.profundidade>0)
                {
                    //printMatriz(aux);
                    solucao=solucao+aux.movimento_anterior;
                    aux=aux.pai;
                }
                System.out.print("Solução: ");
                printCaminho(solucao);
                break;
            }

            else gerarFilhos(pilha.pop());
        }
    }

    public static void estrela()
    {
        heap.add(mi);
        while(heap.peek()!=null)
        {
            if(heap.peek().key.equals(fi.key))
            {
                Matriz aux = heap.peek();
                System.out.println("Profundidade: " + aux.profundidade);

                while(aux.profundidade>0)
                {
                    //printMatriz(aux);
                    solucao=solucao+aux.movimento_anterior;
                    aux=aux.pai;
                }
                System.out.print("Solução: ");
                printCaminho(solucao);
                break;

            }

            else gerarFilhos(heap.poll());
        }
    }


    public static void gerarFilhos(Matriz a)
    {
        int [][] matriz_aux = new int[4][4];
        matriz_aux=copiaMatriz(a.matriz);

        if(a.coluna0-1>=0)
        {
            matriz_aux[a.linha0][a.coluna0]=matriz_aux[a.linha0][a.coluna0-1];
            matriz_aux[a.linha0][a.coluna0-1]=0;
            Matriz filho = new Matriz(matriz_aux,a.profundidade+1,a,'e',a.linha0,a.coluna0-1);
            if(opcao==1)
            {
            	if(!verificaPais(filho))
            	{
            		fila.add(filho);
            	}
            }

            if(opcao==2) //Aqui vemos se já existe antecessor igual e limitamos a profundidade a 25
            {
                if(!verificaPais(filho) && filho.profundidade<25)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==3)
            {
                if(!verificaPais(filho) && filho.profundidade<=limite_iterativo)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==4 || opcao==5)
            {
                filho.heuristica=distSolucao(filho,fi.matriz);
                if(opcao==4) filho.heuristica+=filho.profundidade;
                if(!verificaPais(filho))
                {
                    heap.add(filho);
                }
            }


            matriz_aux=copiaMatriz(a.matriz);
        }

        if(a.coluna0+1<4)
        {
            matriz_aux[a.linha0][a.coluna0]=matriz_aux[a.linha0][a.coluna0+1];
            matriz_aux[a.linha0][a.coluna0+1]=0;
            Matriz filho = new Matriz(matriz_aux,a.profundidade+1,a,'d',a.linha0,a.coluna0+1);
            if(opcao==1)
            {
            	if(!verificaPais(filho))
            	{
            		fila.add(filho);
            	}
            }
            if(opcao==2) //Aqui vemos se já existe antecessor igual e limitamos a profundidade a 25
            {
                if(!verificaPais(filho) && filho.profundidade<25)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==3)
            {
                if(!verificaPais(filho) && filho.profundidade<=limite_iterativo)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==4 || opcao==5)
            {
                filho.heuristica=distSolucao(filho,fi.matriz);
                if(opcao==4) filho.heuristica+=filho.profundidade;
                if(!verificaPais(filho))
                {
                    heap.add(filho);
                }
            }

            matriz_aux=copiaMatriz(a.matriz);
        }


        if(a.linha0-1>=0)
        {
            matriz_aux[a.linha0][a.coluna0]=matriz_aux[a.linha0-1][a.coluna0];
            matriz_aux[a.linha0-1][a.coluna0]=0;
            Matriz filho = new Matriz(matriz_aux,a.profundidade+1,a,'c',a.linha0-1,a.coluna0);
            if(opcao==1)
            {
            	if(!verificaPais(filho))
            	{
            		fila.add(filho);
            	}
            }
            if(opcao==2) //Aqui vemos se já existe antecessor igual e limitamos a profundidade a 25
            {
                if(!verificaPais(filho) && filho.profundidade<25)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==3)
            {
                if(!verificaPais(filho) && filho.profundidade<=limite_iterativo)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==4 || opcao==5)
            {
                filho.heuristica=distSolucao(filho,fi.matriz);
                if(opcao==4) filho.heuristica+=filho.profundidade;
                if(!verificaPais(filho))
                {
                    heap.add(filho);
                }
            }

            matriz_aux=copiaMatriz(a.matriz);
        }

        if(a.linha0+1<4)
        {
            matriz_aux[a.linha0][a.coluna0]=matriz_aux[a.linha0+1][a.coluna0];
            matriz_aux[a.linha0+1][a.coluna0]=0;
            Matriz filho = new Matriz(matriz_aux,a.profundidade+1,a,'b',a.linha0+1,a.coluna0);
            if(opcao==1)
            {
            	if(!verificaPais(filho))
            	{
            		fila.add(filho);
            	}
            }
            if(opcao==2) //Aqui vemos se já existe antecessor igual e limitamos a profundidade a 25
            {
                if(!verificaPais(filho) && filho.profundidade<25)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==3)
            {
                if(!verificaPais(filho) && filho.profundidade<=limite_iterativo)
                {
                     pilha.push(filho);
                }

            }

            if(opcao==4 || opcao==5)
            {
                filho.heuristica=distSolucao(filho,fi.matriz);
                if(opcao==4) filho.heuristica+=filho.profundidade;
                if(!verificaPais(filho))
                {
                    heap.add(filho);
                }
            }

            matriz_aux=copiaMatriz(a.matriz);
        }


    }
    
    public static void printCaminho(String solucao)
    {
    	for(int i=solucao.length()-1;i>=0;i--)
    	{
    		System.out.print(solucao.charAt(i));
    	}
    	System.out.println();
    }

    public static int [][] copiaMatriz(int matriz[][])
    {
        int [][] aux=new int[4][4];
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                aux[i][j]=matriz[i][j];

        return aux;
    }


    public static void printMatriz (Matriz a)
    {
        System.out.println("Profundidade: " + a.profundidade);
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
                {
                    System.out.print(a.matriz[i][j] + " ");
                }
                System.out.println();
        }
        System.out.println();
    }

    public static boolean verificaPais(Matriz a)
    {
        String key;
        key=a.key;
        a=a.pai;
        while(a.pai!=null)
        {
            if(key.equals(a.key))
            {
                return true;
            }
            a=a.pai;
        }

        return false;
    }



    public static boolean Solvabilidade(int [][] matriz_inicial, int [][] matriz_final)
    {
        int k=0,inversoes_inicial=0,inversoes_final=0,zero_inicial=0,zero_final=0;
        int vetor_inicial [] = new int [16];
        int vetor_final [] = new int [16];
        boolean first=false,last=false;
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                if(matriz_inicial[i][j]==0) zero_inicial=i;
                if(matriz_final[i][j]==0) zero_final=i;
                vetor_inicial[k]=matriz_inicial[i][j];
                vetor_final[k]=matriz_final[i][j];
                k++;
            }

        for(int i=0;i<16;i++)
            for(int j=i+1;j<16;j++)
            {
                if(vetor_inicial[i]>vetor_inicial[j] && vetor_inicial[j]!=0) inversoes_inicial++;
            }


        for(int i=0;i<16;i++)
            for(int j=i+1;j<16;j++)
            {
                if(vetor_final[i]>vetor_final[j] && vetor_final[j]!=0) inversoes_final++;
            }


		if((zero_inicial%2 == 0 && inversoes_inicial%2 != 0) || (zero_inicial%2 != 0 && inversoes_inicial%2 == 0))
			first = true;
		if((zero_final%2 == 0 && inversoes_final%2 != 0) || (zero_final%2 != 0 && inversoes_final%2 == 0))
			last = true;
		return first == last;


    }


    public static int distSolucao(Matriz matriz, int [][] matriz_final)
    {
        int [][] aux = new int [4][4];
        int k,p,total=0;
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                for(k=0;k<4;k++)
                    for(p=0;p<4;p++)
                    {
                        if(matriz.matriz[i][j] == matriz_final[k][p])
                        {
                            aux[i][j]=Math.abs(k-i) + Math.abs(p-j);
                        }
                    }
            }


            for(int i=0;i<4;i++)
                for(int j=0;j<4;j++)
                    total=total+aux[i][j];

            return total;

    }

}