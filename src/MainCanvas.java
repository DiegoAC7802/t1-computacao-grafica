import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import core3d.Mat4x4;
import core3d.Triangulo3D;

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

	float velocidade = 320.0f;
	float cameraX = 0;
	float cameraZ = 0;
	float cameraYaw = 0;

	// OBJETO 3D

	ArrayList<Objeto3D> objetos = new ArrayList<>();

	// MATRIZ DA CÂMERA / CENA

	Mat4x4 modelview;

	// MATRIZ DE PROJEÇÃO

	Mat4x4 projecao;

	// CONSTRUTOR

	public MainCanvas() {

		setSize(W, H);
		setFocusable(true);
		Triangulo3D.defineAlturaViewport(H);

		// Matriz ModelView

		modelview = new Mat4x4();
		modelview.setIdentity();

		// Matriz de projeção

		projecao = new Mat4x4();
		projecao.setParalelProjection();

		// CARREGAMENTO DA CENA

		try {
			adicionaObjeto("obj/medieval house.obj", 0.60f, -20, 0, 10);
			adicionaObjeto("obj/tank.obj", 0.12f, 30, 0, -25);
			adicionaObjeto("obj/uploads_files_2787791_Mercedes+Benz+GLS+580.obj", 2.8f, 55, 0, 15);
			adicionaObjeto("obj/chair_01.obj", 18.0f, 10, 0, 35);
			adicionaObjeto("obj/SR71.obj", 0.42f, -50, 20, -70);
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

				if (key == KeyEvent.VK_W) UP = false;
				if (key == KeyEvent.VK_S) DOWN = false;

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

				if (key == KeyEvent.VK_W) UP = true;
				if (key == KeyEvent.VK_S) DOWN = true;

				if (key == KeyEvent.VK_A) {
					LEFT = true;
				}

				if (key == KeyEvent.VK_D) {
					RIGHT = true;
				}

				if (key == KeyEvent.VK_Q) cameraYaw -= 5;
				if (key == KeyEvent.VK_E) cameraYaw += 5;

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
		float movimento = velocidade * diftime / 1000.0f;
		float frente = (UP ? 1 : 0) - (DOWN ? 1 : 0);
		float lateral = (RIGHT ? 1 : 0) - (LEFT ? 1 : 0);
		float rad = (float) Math.toRadians(cameraYaw);

		cameraX += (float) ((Math.cos(rad) * lateral) + (Math.sin(rad) * frente)) * movimento;
		cameraZ += (float) ((Math.sin(rad) * lateral) - (Math.cos(rad) * frente)) * movimento;
		atualizaCamera();
	}

	private void adicionaObjeto(String caminho, float escala, float x, float y, float z)
			throws IOException {
		Objeto3D novo = new Objeto3D(caminho);
		novo.escala(escala, escala, escala);
		novo.translacao(x, y, z);
		objetos.add(novo);
	}

	private void atualizaCamera() {
		Mat4x4 camera = new Mat4x4();
		camera.setIdentity();

		Mat4x4 rotacao = new Mat4x4();
		rotacao.setRotateY(-cameraYaw);
		camera = camera.multiplicaMatrizes(camera, rotacao);

		Mat4x4 translacao = new Mat4x4();
		translacao.setTranslate(-cameraX, 0, -cameraZ);
		camera = camera.multiplicaMatrizes(camera, translacao);

		Mat4x4 centraliza = new Mat4x4();
		centraliza.setTranslate(W / 2.0f, H / 2.0f, 0);
		camera = camera.multiplicaMatrizes(camera, centraliza);

		modelview = camera;
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

		for (Objeto3D objeto : objetos) {
			objeto.desenha((Graphics2D) g, modelview, projecao);
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