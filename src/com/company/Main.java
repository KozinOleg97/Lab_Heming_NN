package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;

public class Main {

    public static double[][] W = new double[10][10];//веса
    public static double[][] Weps = new double[10][10];//веса eps
    public static int[][] Class = new int[10][10];//классы
    public static double[][] S = new double[10][10];//состояния
    public static int Kol = 0;
    public static int m = 0;
    public static double T = 0;
    public static int iter = 0;
    public static int Era = 0;//общее кол-во эпох
    public static double eps = 0;
    public static double Emax = 0;

    public static void main(String[] args) throws IOException {
        String str = "";
        do {

            System.out.println("1) - Обучение сети.");
            System.out.println("2) - Тест.");
            System.out.println("3) - Выход.");

            Scanner in = new Scanner(System.in);

            str = in.nextLine();
            System.out.println();

            switch (str) {
                case "1": {
                    Learning();
                    break;
                }
                case "2": {
                    Hamming();
                    break;
                }
            }

        } while (str != "3");
    }


    public static void Learning() throws IOException//1) - Обучение сети.
    {

        File file1 = new File("data.txt");
        Scanner file = new Scanner(file1);

        Integer cur;

        while (file.hasNext()) {
            // Console.Write("Введите кол-во классов для обучения: ");
            //String str = file.ReadLine();
            //String a = file.nextLine();
            Kol = file.nextInt();

            //Console.Write("Введите размерность вектаров: ");
            //str = file.ReadLine();
            m = file.nextInt();

            for (int i = 0; i < Kol; i++) {
                //System.out.println("Введите вектор:");
                for (int j = 0; j < m; j++)
                    Class[i][j] = file.nextInt();
            }
        }
        System.out.println("Обучился!");
        file.close();

        T = (double) m / 2;
        //матрица весов
        for (int i = 0; i < Kol; i++) {
            for (int j = 0; j < m; j++) {
                W[i][j] = (double) Class[i][j] / 2;
                Weps[i][j] = 1;
                if (i != j) Weps[i][j] /= Kol;
            }
        }
        eps = 1.0 / Kol;
    }


    public static void Hamming()//2) - Тест.
    {
        int[] x = new int[m];
        Write_Test(x);//ввод данных для теста
        //подсчет S 1-го слоя
        for (int j = 0; j < Kol; j++) {
            S[0][j] = 0;
            for (int t = 0; t < m; t++)
                S[0][j] += W[j][t] * x[t];
            S[0][j] += T;
        }
        iter++;
        Normalizing();
        System.out.println("**************Вывод************");
        System.out.print("1 - слой:");
        Res_Test();//вывод промежуточных слоев

        //подсчет 2-го слоя
        for (int j = 0; j < Kol; j++) {
            S[1][j] = S[0][j];
            double sum = 0;
            for (int t = 0; t < Kol; t++)
                if (j != t) sum += S[0][t];
            S[1][j] -= eps * sum;
        }
        iter++;
        Normalizing();

        System.out.print("2 - слой:");
        Res_Test();//вывод промежуточных слоев

        Fun_While();
    }


    public static void Write_Test(int[] x) {
            /*System.out.println("Введите класс для теста:");
            String str = "";
            for (int i = 0; i < m; i++)
            {
                str = Console.ReadLine();
                x[i] = Convert.ToInt32(str);
            }*/
        //тест с лекции
        x[0] = 1;
        x[1] = 1;
        x[2] = 1;
        x[3] = 1;
        x[4] = -1;
        x[5] = 1;
        x[6] = 1;
        x[7] = 1;
        x[8] = 1;
    }


    public static void Res_Test() {
        for (int i = 0; i < Kol; i++)
            System.out.print(S[iter - 1][i] + " ");
        System.out.println();
    }


    public static void Normalizing() {
        for (int j = 0; j < Kol; j++) {
            if (S[iter - 1][j] <= 0) S[iter - 1][j] = 0;
            if ((S[iter - 1][j] > 0) && (S[iter - 1][j] <= T)) S[iter - 1][j] = S[iter - 1][j];
            if (S[iter - 1][j] > T) S[iter - 1][j] = T;
        }
    }


    public static void Fun_While() {
        Emax = 0;
        for (int i = 0; i < Kol; i++)
            Emax += Math.pow(S[iter - 2][i] - S[iter - 1][i], 2);
        System.out.println("Emax - " + Emax);
        Era = iter;
        while (Emax > 0.1) {
            for (int i = 0; i < Kol; i++) {
                S[iter - 2][i] = S[iter - 1][i];
                S[iter - 1][i] = 0;
            }
            iter = 1;

            //подсчет n-го слоя
            for (int j = 0; j < Kol; j++) {
                S[1][j] = S[0][j];
                double sum = 0;
                for (int t = 0; t < Kol; t++)
                    if (j != t) sum += S[0][t];
                S[1][j] -= eps * sum;
            }
            iter++;
            Era++;
            Normalizing();

            System.out.print(Era + ") " + " 2 - слой:");
            Res_Test();//вывод промежуточных слоев


            Emax = 0;
            for (int i = 0; i < Kol; i++)
                Emax += Math.pow(S[iter - 2][i] - S[iter - 1][i], 2);
            System.out.println("Emax - " + Emax);
        }

        Write_Exit();
    }


    public static void Write_Exit() {
        System.out.println("Классы на которых обучали:");
        for (int i = 0; i < Kol; i++) {
            System.out.print((i + 1) + ") ");
            for (int j = 0; j < m; j++)
                System.out.print(Class[i][j] + " ");
            System.out.println();
        }

        System.out.println("Кол-во циклов: " + Era);
        System.out.println("Emax - " + Emax);

        int sum = 0;
        System.out.println("Выходной вектор: ");
        for (int i = 0; i < Kol; i++) {
            if (S[iter - 2][i] != 0) sum++;
            System.out.print(S[iter - 2][i] + " ");
        }
        System.out.println();

        if (sum == 1) {
            for (int i = 0; i < Kol; i++)
                if (S[iter - 2][i] != 0)
                    System.out.println("Эта последовательность принадлежит " + (i + 1) + " классу.");
        } else System.out.println("Эта последовательность не определена.");

        System.out.println("**************Конец************");
    }
}

