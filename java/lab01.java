import java.util.Random;

public class lab01 {
    public static void main(String[] args) {
        //1
        short g[] = new short[11];
        short j = 22;
        for (int i = 0; i<g.length; i++){
            g[i] = j;
            j-=2;
        }
        //2
        double x[] = new double[12];
        Random random = new Random();
        for (int i = 0; i < x.length; i++){
            x[i] = -2.0 + (random.nextDouble() * (6.0));
        }
        //3
        double[][] a = new double[11][12];
        for (int line = 0; line <11; line++){
            for (int row = 0; row<12; row++){
                switch (g[line]) {
                    case 10:
                        a[line][row] = Math.sin(Math.cos(Math.pow(x[row], 1.0 / (2.0) -x[row])));
                        break;
                    case 4:
                    case 8:
                    case 14:
                    case 20:
                    case 22:
                        a[line][row] = Math.log(Math.sqrt(Math.pow(Math.cos(x[row]), 2)));
                        break;
                    default:
                        a[line][row] = Math.cos(Math.cos(Math.exp(Math.exp(x[row]))));
                        break;
                }
            }
        }

        //4
        for (double[] a1 : a) {
            for (double a2: a1) {
                System.out.printf("%.4f ", a2);
            }
            System.out.println();
        }
    }
}

