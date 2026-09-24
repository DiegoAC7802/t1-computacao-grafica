import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JPanel;

import core3d.Mat4x4;

public class MainCanvas extends JPanel implements Runnable {

	int W = 1920;
	int H = 1080;

	Thread runner;
	boolean ativo = true;

	int paintcounter = 0;

	int framecount = 0;
	int fps = 0;

	Font f = new Font("", Font.PLAIN, 20);

	// MOUSE

	int mouseX = 0;
	int mouseY = 0;

	// MOVIMENTAÇÃO

	boolean LEFT = false;
	boolean RIGHT = false;
	boolean UP = false;
	boolean DOWN = false;

	// VELOCIDADE

	float velocidade = 50.0f;

	// OBJETO 3D

	Objeto3D objeto;

	// MATRIZ DA CÂMERA / CENA

	Mat4x4 modelview;

	// MATRIZ DE PROJEÇÃO

	Mat4x4 projecao;

	// CONSTRUTOR

	public MainCanvas() {

		setSize(W, H);
		setFocusable(true);

		// Matriz ModelView

		modelview = new Mat4x4();
		modelview.setIdentity();

		// Matriz de projeção

		projecao = new Mat4x4();
		projecao.setParalelProjection();

		// CARREGAMENTO DO OBJETO

		try {

			objeto =
				new Objeto3D(
					"obj\\medieval house.obj"
				);

		} catch (IOException e) {

			e.printStackTrace();
		}


		// TECLADO

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				int key = e.getKeyCode();

				if (key == KeyEvent.VK_S) {
					UP = false;
				}

				if (key == KeyEvent.VK_W) {
					DOWN = false;
				}

				if (key == KeyEvent.VK_A) {
					LEFT = false;
				}

				if (key == KeyEvent.VK_D) {
					RIGHT = false;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

				int key = e.getKeyCode();

				// MOVIMENTAÇÃO

				if (key == KeyEvent.VK_S) {
					UP = true;
				}

				if (key == KeyEvent.VK_W) {
					DOWN = true;
				}

				if (key == KeyEvent.VK_A) {
					LEFT = true;
				}

				if (key == KeyEvent.VK_D) {
					RIGHT = true;
				}

				// ESCALA -

				if (key == KeyEvent.VK_Z) {

					Mat4x4 matrot =
						new Mat4x4();

					matrot.setSacale(
						0.8f,
						0.8f,
						0.8f
					);

					modelview =
						modelview.multiplicaMatrizes(
							matrot,
							modelview
						);
				}

				// ESCALA +

				if (key == KeyEvent.VK_X) {

					Mat4x4 matrot =
						new Mat4x4();

					matrot.setSacale(
						1.2f,
						1.2f,
						1.2f
					);

					modelview =
						modelview.multiplicaMatrizes(
							matrot,
							modelview
						);
				}

				// ROTAÇÃO PARA ESQUERDA

				if (key == KeyEvent.VK_Q) {

					Mat4x4 matrot =
						new Mat4x4();

					matrot.setRotateY(-5);

					modelview =
						modelview.multiplicaMatrizes(
							matrot,
							modelview
						);
				}

				// ROTAÇÃO PARA DIREITA

				if (key == KeyEvent.VK_E) {

					Mat4x4 matrot =
						new Mat4x4();

					matrot.setRotateY(+5);

					modelview =
						modelview.multiplicaMatrizes(
							matrot,
							modelview
						);
				}

				// ROTAÇÃO PARA CIMA

				if (key == KeyEvent.VK_O) {

					Mat4x4 matrot =
						new Mat4x4();

					matrot.setRotateX(-5);

					modelview =
						modelview.multiplicaMatrizes(
							matrot,
							modelview
						);
				}

				// ROTAÇÃO PARA BAIXO

				if (key == KeyEvent.VK_P) {

					Mat4x4 matrot =
						new Mat4x4();

					matrot.setRotateX(+5);

					modelview =
						modelview.multiplicaMatrizes(
							matrot,
							modelview
						);
				}

				// PROJEÇÃO PARALELA

				if (key == KeyEvent.VK_1) {

					projecao.setParalelProjection();
				}

				// PROJEÇÃO OBLÍQUA

				if (key == KeyEvent.VK_2) {

					projecao.setObliqueProjection(
						1,
						45
					);
				}

				// OUTRA PROJEÇÃO OBLÍQUA

				if (key == KeyEvent.VK_3) {

					projecao.setObliqueProjection(
						0.5f,
						30
					);
				}
			}
		});


		// MOUSE

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {

				mouseX = e.getX();
				mouseY = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {

				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
	}


	// SIMULAÇÃO DO MUNDO

	public void simulaMundo(long diftime) {

		// MOVIMENTAÇÃO

		if (UP || DOWN || LEFT || RIGHT) {

			float difS =
				diftime / 1000.0f;

			float movimento =
				velocidade * difS;

			Mat4x4 matrot =
				new Mat4x4();

			if (UP) {

				matrot.setTranslate(
					0,
					-movimento,
					0
				);
			}

			if (DOWN) {

				matrot.setTranslate(
					0,
					movimento,
					0
				);
			}

			if (LEFT) {

				matrot.setTranslate(
					-movimento,
					0,
					0
				);
			}

			if (RIGHT) {

				matrot.setTranslate(
					movimento,
					0,
					0
				);
			}

			modelview =
				modelview.multiplicaMatrizes(
					matrot,
					modelview
				);
		}
	}


	// DESENHO

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		// FUNDO

		g.setColor(Color.white);

		g.fillRect(
			0,
			0,
			W,
			H
		);


		// DESENHO DO OBJETO

		g.setColor(Color.black);

		if (objeto != null) {

			objeto.desenha(
				(Graphics2D) g,
				modelview,
				projecao
			);
		}


		// ESTATÍSTICAS

		g.setColor(Color.black);

		g.setFont(f);

		g.drawString(
			"FPS " + fps,
			10,
			25
		);

		g.drawString(
			"mouse: " + mouseX + "," + mouseY,
			10,
			50
		);
	}


	// LOOP

	public void start() {

		runner =
			new Thread(this);

		runner.start();
	}


	@Override
	public void run() {

		long time =
			System.currentTimeMillis();

		long segundo =
			time / 1000;

		long diftime = 0;

		while (ativo) {

			// ATUALIZA MUNDO

			simulaMundo(diftime);


			// DESENHA

			paintImmediately(
				0,
				0,
				W,
				H
			);

			paintcounter += 100;


			// CONTROLE DO LOOP

			try {

				Thread.sleep(1);

			} catch (InterruptedException e) {

				Thread.currentThread().interrupt();

				break;
			}


			// TEMPO

			long newtime =
				System.currentTimeMillis();

			long novoSegundo =
				newtime / 1000;

			diftime =
				newtime - time;

			time =
				newtime;


			// FPS

			framecount++;

			if (novoSegundo != segundo) {

				fps = framecount;

				framecount = 0;

				segundo = novoSegundo;
			}
		}
	}
}