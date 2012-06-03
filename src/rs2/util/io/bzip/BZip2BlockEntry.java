/*    */ package rs2.util.io.bzip;
/*    */ 
/*    */ public class BZip2BlockEntry
/*    */ {
/*    */   byte[] inputBuffer;
/*    */   int offset;
/*    */   int compressedSize;
/*    */   int anInt566;
/*    */   int anInt567;
/*    */   byte[] outputBuffer;
/*    */   int anInt569;
/*    */   int decompressedSize;
/*    */   int anInt571;
/*    */   int anInt572;
/*    */   byte aByte573;
/*    */   int anInt574;
/*    */   boolean wasRandomised;
/*    */   int anInt576;
/*    */   int anInt577;
/*    */   int blockSize100k;
/*    */   int anInt579;
/*    */   int origPtr;
/*    */   int anInt581;
/*    */   int anInt582;
/*    */   final int[] unzftab;
/*    */   int anInt584;
/*    */   final int[] anIntArray585;
/*    */   public static int[] ll8;
/*    */   int inUseOffset;
/*    */   final boolean[] inUse;
/*    */   final boolean[] inUse16;
/*    */   final byte[] seqToUnseq;
/*    */   final byte[] yy;
/*    */   final int[] anIntArray593;
/*    */   final byte[] selector;
/*    */   final byte[] selectorMtf;
/*    */   final byte[][] len;
/*    */   final int[][] limit;
/*    */   final int[][] base;
/*    */   final int[][] perm;
/*    */   final int[] minLens;
/*    */   int anInt601;
/*    */ 
/*    */   BZip2BlockEntry()
/*    */   {
/* 10 */     this.unzftab = new int[256];
/* 11 */     this.anIntArray585 = new int[257];
/* 12 */     this.inUse = new boolean[256];
/* 13 */     this.inUse16 = new boolean[16];
/* 14 */     this.seqToUnseq = new byte[256];
/* 15 */     this.yy = new byte[4096];
/* 16 */     this.anIntArray593 = new int[16];
/* 17 */     this.selector = new byte[18002];
/* 18 */     this.selectorMtf = new byte[18002];
/* 19 */     this.len = new byte[6][258];
/* 20 */     this.limit = new int[6][258];
/* 21 */     this.base = new int[6][258];
/* 22 */     this.perm = new int[6][258];
/* 23 */     this.minLens = new int[6];
/*    */   }
/*    */ }

/* Location:           C:\Users\Galkon\Dropbox\RSPS\Tools\Toms Suite\CacheSuite Jarred\Toms Suite.jar
 * Qualified Name:     com.jagex.cache.util.bzip.BZip2BlockEntry
 * JD-Core Version:    0.6.0
 */