package rs2.util;

public class Quantize {

	static final boolean QUICK = true;
	static final int MAX_RGB = 255;
	static final int MAX_NODES = 266817;
	static final int MAX_TREE_DEPTH = 8;
	static int[] SQUARES = new int[511];
	static int[] SHIFT;

	public static int[] quantizeImage(int[][] pixels, int max_colors) {
		Cube cube = new Cube(pixels, max_colors);
		cube.classification();
		cube.reduction();
		cube.assignment();
		return cube.colormap;
	}

	static {
		for (int i = -255; i <= 255; i++) {
			SQUARES[(i + 255)] = (i * i);
		}
		SHIFT = new int[9];
		for (int i = 0; i < 9; i++)
			SHIFT[i] = (1 << 15 - i);
	}

	static class Cube {
		int[][] pixels;
		int max_colors;
		int[] colormap;
		Node root;
		int depth;
		int colors;
		int nodes;

		Cube(int[][] pixels, int max_colors) {
			/* 293 */this.pixels = pixels;
			/* 294 */this.max_colors = max_colors;

			/* 296 */int i = max_colors;

			/* 299 */for (this.depth = 1; i != 0; this.depth += 1) {
				/* 300 */i /= 4;
			}
			/* 302 */if (this.depth > 1) {
				/* 303 */this.depth -= 1;
			}
			/* 305 */if (this.depth > 8)
				/* 306 */this.depth = 8;
			/* 307 */else if (this.depth < 2) {
				/* 308 */this.depth = 2;
			}

			/* 311 */this.root = new Node(this);
		}

		void classification() {
			/* 353 */int[][] pixels = this.pixels;

			/* 355 */int width = pixels.length;
			/* 356 */int height = pixels[0].length;
			int y;

			/* 359 */for (int x = width; x-- > 0;)
				/* 360 */for (y = height; y-- > 0;) {
					/* 361 */int pixel = pixels[x][y];
					/* 362 */int red = pixel >> 16 & 0xFF;
					/* 363 */int green = pixel >> 8 & 0xFF;
					/* 364 */int blue = pixel >> 0 & 0xFF;

					/* 367 */if (this.nodes > 266817) {
						/* 368 */System.out.println("pruning");
						/* 369 */this.root.pruneLevel();
						/* 370 */this.depth -= 1;
					}

					/* 375 */Node node = this.root;
					/* 376 */for (int level = 1; level <= this.depth; level++) {
						/* 377 */int id = (red > node.mid_red ? 1 : 0) << 0
								| (green > node.mid_green ? 1 : 0) << 1
								| (blue > node.mid_blue ? 1 : 0) << 2;

						/* 380 */if (node.child[id] == null) {
							/* 381 */new Node(node, id, level);
						}
						/* 383 */node = node.child[id];
						/* 384 */node.number_pixels += Quantize.SHIFT[level];
					}

					/* 387 */node.unique += 1;
					/* 388 */node.total_red += red;
					/* 389 */node.total_green += green;
					/* 390 */node.total_blue += blue;
				}
		}

		void reduction() {
			/* 408 */int threshold = 1;
			/* 409 */while (this.colors > this.max_colors) {
				/* 410 */this.colors = 0;
				/* 411 */threshold = this.root.reduce(threshold, 2147483647);
			}
		}

		void assignment() {
			/* 444 */this.colormap = new int[this.colors];

			/* 446 */this.colors = 0;
			/* 447 */this.root.colormap();

			/* 449 */int[][] pixels = this.pixels;

			/* 451 */int width = pixels.length;
			/* 452 */int height = pixels[0].length;

			int y;

			/* 457 */for (int x = width; x-- > 0;)
				/* 458 */for (y = height; y-- > 0;) {
					/* 459 */int pixel = pixels[x][y];
					/* 460 */int red = pixel >> 16 & 0xFF;
					/* 461 */int green = pixel >> 8 & 0xFF;
					/* 462 */int blue = pixel >> 0 & 0xFF;

					/* 465 */Node node = this.root;
					while (true) {
						/* 467 */int id = (red > node.mid_red ? 1 : 0) << 0
								| (green > node.mid_green ? 1 : 0) << 1
								| (blue > node.mid_blue ? 1 : 0) << 2;

						/* 470 */if (node.child[id] == null) {
							break;
						}
						/* 473 */node = node.child[id];
					}

					/* 480 */pixels[x][y] = node.color_number;
				}
		}

		static class Node {
			Quantize.Cube cube;
			Node parent;
			Node[] child;
			int nchild;
			int id;
			int level;
			int mid_red;
			int mid_green;
			int mid_blue;
			int number_pixels;
			int unique;
			int total_red;
			int total_green;
			int total_blue;
			int color_number;

			Node(Quantize.Cube cube) {
				/* 527 */this.cube = cube;
				/* 528 */this.parent = this;
				/* 529 */this.child = new Node[8];
				/* 530 */this.id = 0;
				/* 531 */this.level = 0;

				/* 533 */this.number_pixels = 2147483647;

				/* 535 */this.mid_red = 128;
				/* 536 */this.mid_green = 128;
				/* 537 */this.mid_blue = 128;
			}

			Node(Node parent, int id, int level) {
				/* 541 */this.cube = parent.cube;
				/* 542 */this.parent = parent;
				/* 543 */this.child = new Node[8];
				/* 544 */this.id = id;
				/* 545 */this.level = level;

				/* 548 */this.cube.nodes += 1;
				/* 549 */if (level == this.cube.depth) {
					/* 550 */this.cube.colors += 1;
				}

				/* 554 */parent.nchild += 1;
				/* 555 */parent.child[id] = this;

				/* 558 */int bi = 1 << 8 - level >> 1;
				/* 559 */parent.mid_red += ((id & 0x1) > 0 ? bi : -bi);
				/* 560 */parent.mid_green += ((id & 0x2) > 0 ? bi : -bi);
				/* 561 */parent.mid_blue += ((id & 0x4) > 0 ? bi : -bi);
			}

			void pruneChild() {
				/* 569 */this.parent.nchild -= 1;
				/* 570 */this.parent.unique += this.unique;
				/* 571 */this.parent.total_red += this.total_red;
				/* 572 */this.parent.total_green += this.total_green;
				/* 573 */this.parent.total_blue += this.total_blue;
				/* 574 */this.parent.child[this.id] = null;
				/* 575 */this.cube.nodes -= 1;
				/* 576 */this.cube = null;
				/* 577 */this.parent = null;
			}

			void pruneLevel() {
				/* 584 */if (this.nchild != 0) {
					/* 585 */for (int id = 0; id < 8; id++) {
						/* 586 */if (this.child[id] != null) {
							/* 587 */this.child[id].pruneLevel();
						}
					}
				}
				/* 591 */if (this.level == this.cube.depth)
					/* 592 */pruneChild();
			}

			int reduce(int threshold, int next_threshold) {
				/* 604 */if (this.nchild != 0) {
					/* 605 */for (int id = 0; id < 8; id++) {
						/* 606 */if (this.child[id] != null) {
							/* 607 */next_threshold = this.child[id].reduce(
									threshold, next_threshold);
						}
					}
				}
				/* 611 */if (this.number_pixels <= threshold) {
					/* 612 */pruneChild();
				} else {
					/* 614 */if (this.unique != 0) {
						/* 615 */this.cube.colors += 1;
					}
					/* 617 */if (this.number_pixels < next_threshold) {
						/* 618 */next_threshold = this.number_pixels;
					}
				}
				/* 621 */return next_threshold;
			}

			void colormap() {
				/* 631 */if (this.nchild != 0) {
					/* 632 */for (int id = 0; id < 8; id++) {
						/* 633 */if (this.child[id] != null) {
							/* 634 */this.child[id].colormap();
						}
					}
				}
				/* 638 */if (this.unique != 0) {
					/* 639 */int r = (this.total_red + (this.unique >> 1))
							/ this.unique;
					/* 640 */int g = (this.total_green + (this.unique >> 1))
							/ this.unique;
					/* 641 */int b = (this.total_blue + (this.unique >> 1))
							/ this.unique;
					/* 642 */this.cube.colormap[this.cube.colors] = (0xFF000000
							| (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0);

					/* 646 */this.color_number = (this.cube.colors++);
				}
			}

			void closestColor(int red, int green, int blue,
					Quantize.Cube.Search search) {
				/* 655 */if (this.nchild != 0) {
					/* 656 */for (int id = 0; id < 8; id++) {
						/* 657 */if (this.child[id] != null) {
							/* 658 */this.child[id].closestColor(red, green,
									blue, search);
						}
					}
				}

				/* 663 */if (this.unique != 0) {
					/* 664 */int color = this.cube.colormap[this.color_number];
					/* 665 */int distance = distance(color, red, green, blue);
					/* 666 */if (distance < search.distance) {
						/* 667 */search.distance = distance;
						/* 668 */search.color_number = this.color_number;
					}
				}
			}

			static final int distance(int color, int r, int g, int b) {
				return Quantize.SQUARES[((color >> 16 & 0xFF) - r + 255)] + Quantize.SQUARES[((color >> 8 & 0xFF) - g + 255)] + Quantize.SQUARES[((color >> 0 & 0xFF) - b + 255)];
			}

			public String toString() {
				StringBuffer buf = new StringBuffer();
				if (this.parent == this)
					buf.append("root");
				else {
					buf.append("node");
				}
				buf.append(' ');
				buf.append(this.level);
				buf.append(" [");
				buf.append(this.mid_red);
				buf.append(',');
				buf.append(this.mid_green);
				buf.append(',');
				buf.append(this.mid_blue);
				buf.append(']');
				return new String(buf);
			}
		}

		static class Search {
			int distance;
			int color_number;
		}
	}
}