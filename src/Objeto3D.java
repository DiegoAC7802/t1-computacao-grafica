import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;

import core3d.Mat4x4;
import core3d.Triangulo3D;

public class Objeto3D {

	ArrayList<Triangulo3D> triangulos;

	Mat4x4 modelview;


	// ========================================================
	// CONSTRUTOR
	// ========================================================

	public Objeto3D(String caminhoOBJ) throws IOException {

		triangulos =
			carregadorOBJ.carregar(caminhoOBJ);

		modelview = new Mat4x4();

		modelview.setIdentity();
	}


	// ========================================================
	// TRANSLADAÇÃO
	// ========================================================

	public void translacao(
		float x,
		float y,
		float z
	) {

		Mat4x4 mat = new Mat4x4();

		mat.setTranslate(
			x,
			y,
			z
		);

		modelview =
			modelview.multiplicaMatrizes(
				mat,
				modelview
			);
	}


	// ========================================================
	// ROTAÇÃO X
	// ========================================================

	public void rotacaoX(float angulo) {

		Mat4x4 mat = new Mat4x4();

		mat.setRotateX(angulo);

		modelview =
			modelview.multiplicaMatrizes(
				mat,
				modelview
			);
	}


	// ========================================================
	// ROTAÇÃO Y
	// ========================================================

	public void rotacaoY(float angulo) {

		Mat4x4 mat = new Mat4x4();

		mat.setRotateY(angulo);

		modelview =
			modelview.multiplicaMatrizes(
				mat,
				modelview
			);
	}


	// ========================================================
	// ROTAÇÃO Z
	// ========================================================
    
/* 
	public void rotacaoZ(float angulo) {

		Mat4x4 mat = new Mat4x4();

		mat.setRotateZ(angulo);

		modelview =
			modelview.multiplicaMatrizes(
				mat,
				modelview
			);
	}
*/

	// ========================================================
	// ESCALA
	// ========================================================

	public void escala(
		float x,
		float y,
		float z
	) {

		Mat4x4 mat = new Mat4x4();

		mat.setSacale(
			x,
			y,
			z
		);

		modelview =
			modelview.multiplicaMatrizes(
				mat,
				modelview
			);
	}


	// ========================================================
	// DESENHO
	// ========================================================

	public void desenha(
		Graphics2D g,
		Mat4x4 modelviewGlobal,
		Mat4x4 projecao
	) {

		// ====================================================
		// COMBINA A TRANSFORMAÇÃO DO OBJETO
		// COM A TRANSFORMAÇÃO GLOBAL
		// ====================================================

		Mat4x4 matrizFinal =
			modelviewGlobal.multiplicaMatrizes(
				modelviewGlobal,
				modelview
			);


		// ====================================================
		// DESENHA OS TRIÂNGULOS
		// ====================================================

		for (int i = 0; i < triangulos.size(); i++) {

			Triangulo3D tri =
				triangulos.get(i);

			tri.desenhase(
				g,
				matrizFinal,
				projecao
			);
		}
	}
}
