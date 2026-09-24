import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import core3d.Ponto3D;
import core3d.Triangulo3D;

public class carregadorOBJ {

    public static ArrayList<Triangulo3D> carregar(String caminho)
            throws IOException {

        ArrayList<Ponto3D> vertices = new ArrayList<>();
        ArrayList<Triangulo3D> triangulos = new ArrayList<>();

        BufferedReader reader =
                new BufferedReader(new FileReader(caminho));

        String linha;

        while ((linha = reader.readLine()) != null) {

            linha = linha.trim();

            // Linha vazia
            if (linha.isEmpty()) {
                continue;
            }

            // Comentário
            if (linha.startsWith("#")) {
                continue;
            }

            String[] partes = linha.split("\\s+");

            // ==================================================
            // VÉRTICE
            // ==================================================

            if (partes[0].equals("v")) {

                float x = Float.parseFloat(partes[1]);
                float y = Float.parseFloat(partes[2]);
                float z = Float.parseFloat(partes[3]);

                Ponto3D ponto =
                        new Ponto3D(x, y, z);

                vertices.add(ponto);
            }

            // ==================================================
            // FACE
            // ==================================================

            else if (partes[0].equals("f")) {

                /*
                 * Uma face pode ser:
                 *
                 * f 1 2 3
                 *
                 * ou:
                 *
                 * f 1 2 3 4
                 *
                 * ou:
                 *
                 * f 1/1/1 2/2/2 3/3/3
                 */

                if (partes.length < 4) {
                    continue;
                }

                // Primeiro vértice da face
                int i0 =
                        obterIndiceVertice(
                                partes[1],
                                vertices.size()
                        );

                // Triangulação em "fan"
                //
                // Exemplo:
                //
                // f 1 2 3 4
                //
                // vira:
                //
                // 1 2 3
                // 1 3 4

                for (int i = 2; i < partes.length - 1; i++) {

                    int i1 =
                            obterIndiceVertice(
                                    partes[i],
                                    vertices.size()
                            );

                    int i2 =
                            obterIndiceVertice(
                                    partes[i + 1],
                                    vertices.size()
                            );

                    Ponto3D p0 = vertices.get(i0);
                    Ponto3D p1 = vertices.get(i1);
                    Ponto3D p2 = vertices.get(i2);

                    Triangulo3D triangulo =
                            new Triangulo3D(
                                    p0,
                                    p1,
                                    p2
                            );

                    triangulos.add(triangulo);
                }
            }
        }

        reader.close();

        return triangulos;
    }

    // ==========================================================
    // OBTÉM O ÍNDICE DO VÉRTICE
    // ==========================================================

    private static int obterIndiceVertice(
            String valor,
            int quantidadeVertices) {

        /*
         * Pode receber:
         *
         * 1
         * 1/2
         * 1/2/3
         * 1//3
         */

        String[] partes =
                valor.split("/");

        int indice =
                Integer.parseInt(partes[0]);

        // ======================================================
        // OBJ começa os índices em 1
        //
        // Java começa em 0
        // ======================================================

        if (indice > 0) {
            return indice - 1;
        }

        // ======================================================
        // Índice negativo no OBJ
        //
        // -1 = último vértice
        // -2 = penúltimo
        // etc.
        // ======================================================

        return quantidadeVertices + indice;
    }
}