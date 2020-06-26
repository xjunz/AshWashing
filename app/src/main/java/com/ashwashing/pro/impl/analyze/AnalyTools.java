//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ashwashing.pro.impl.analyze;

import android.util.Log;

public class AnalyTools {
   public static byte[] macBuffer;
   public static int maccountid;
   public static int mdeviceid;
   public static int mproductid;
   public static byte[] tac_timeBuffer;
   public static WaterCodeListener waterCodeLisnter;

   public AnalyTools() {
   }

   public static byte[] analyOriDatas1(byte[] var0) {
      String var1 = DigitalTrans.bytesToHexString(var0);
      if (var1.startsWith("23") && var1.endsWith("0a")) {
         var1 = var1.substring(2, var1.length() - 2);
      } else {
         var1 = null;
      }

      return handleResult2(DigitalTrans.AsciiStringToString(var1));
   }

   public static byte[] analyOriDatas2(byte[] var0) {
      String var1 = DigitalTrans.bytesToHexString(var0);
      if (var1.startsWith("23") && var1.endsWith("0a")) {
         var1 = var1.substring(2, var1.length() - 2);
      } else {
         var1 = null;
      }

      DigitalTrans.AsciiStringToString(var1);
      return DigitalTrans.hexStringToByte(DigitalTrans.AsciiStringToString(var1));
   }

   public static void analyWaterDatas(byte[] var0) {
      byte[] var1 = analyOriDatas1(var0);
      StringBuffer var2 = new StringBuffer();
      boolean var3 = false;
      boolean var4 = false;
      byte var5 = 0;
      byte var6 = 0;
      boolean var7 = false;
      boolean var8 = false;
      boolean var9 = false;
      byte var10 = 0;
      byte var11 = 0;

      int var12;
      for(var12 = 0; var12 < var1.length; ++var12) {
         var2.append(var1[var12]);
      }

      byte[] var13 = analyOriDatas2(var0);
      if (var13 != null && var1 != null) {
         StringBuilder var15;
         StringBuilder var25;
         byte[] var26;
         String var29;
         int var30;
         StringBuilder var35;
         byte[] var38;
         byte[] var42;
         if (var13[4] == -5) {
            String var14;
            String var17;
            String var24;
            String var36;
            String var40;
            String var43;
            label547: {
               if (var1[0] == -128) {
                  if (var1 != null && var1.length == 42) {
                     var2 = new StringBuffer();

                     for(var12 = 0; var12 < var1.length; ++var12) {
                        var2.append(var1[var12]);
                     }

                     var26 = new byte[6];

                     for(var12 = 0; var12 < var26.length; var12 = var30) {
                        var30 = var12 + 1;
                        var26[var12] = (byte)var1[var30];
                     }

                     var14 = DigitalTrans.bytesToHexString(var26);
                     var13 = new byte[4];

                     for(var12 = 0; var12 < var13.length; ++var12) {
                        var13[var12] = (byte)var1[var12 + 7];
                     }

                     var25 = new StringBuilder();
                     var25.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var13)));
                     var25.append("");
                     var25.toString();
                     mproductid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var13));
                     var13 = new byte[4];

                     for(var12 = 0; var12 < var13.length; ++var12) {
                        var13[var12] = (byte)var1[var12 + 11];
                     }

                     var25 = new StringBuilder();
                     var25.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var13)));
                     var25.append("");
                     var25.toString();
                     mdeviceid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var13));
                     var26 = new byte[4];

                     for(var12 = 0; var12 < var26.length; ++var12) {
                        var26[var12] = (byte)var1[var12 + 15];
                     }

                     var35 = new StringBuilder();
                     var35.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26)));
                     var35.append("");
                     var35.toString();
                     maccountid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26));
                     var26 = new byte[1];

                     for(var12 = 0; var12 < var26.length; ++var12) {
                        var26[var12] = (byte)var1[var12 + 19];
                     }

                     var35 = new StringBuilder();
                     var35.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26)));
                     var35.append("");
                     var36 = var35.toString();
                     var26 = new byte[4];

                     for(var12 = 0; var12 < var26.length; ++var12) {
                        var26[var12] = (byte)var1[var12 + 20];
                     }

                     var15 = new StringBuilder();
                     var15.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26)));
                     var15.append("");
                     var15.toString();
                     var30 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26));
                     var38 = new byte[4];

                     for(var12 = 0; var12 < var38.length; ++var12) {
                        var38[var12] = (byte)var1[var12 + 24];
                     }

                     var25 = new StringBuilder();
                     var25.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var38)));
                     var25.append("");
                     var40 = var25.toString();
                     var26 = new byte[4];

                     for(var12 = 0; var12 < var26.length; ++var12) {
                        var26[var12] = (byte)var1[var12 + 28];
                     }

                     StringBuilder var16 = new StringBuilder();
                     var16.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26)));
                     var16.append("");
                     var17 = var16.toString();
                     var26 = new byte[4];

                     for(var12 = 0; var12 < var26.length; ++var12) {
                        var26[var12] = (byte)var1[var12 + 32];
                     }

                     var16 = new StringBuilder();
                     var16.append(DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26)));
                     var16.append("");
                     var29 = var16.toString();
                     var42 = new byte[6];

                     for(var12 = var11; var12 < var42.length; ++var12) {
                        var42[var12] = (byte)var1[var12 + 36];
                     }

                     var43 = DigitalTrans.bytesToHexString(var42);
                     var12 = var30;
                     var3 = true;
                     var24 = var29;
                     break label547;
                  }

                  var25 = new StringBuilder();
                  var25.append("databyte.length:");
                  var25.append(var1.length);
                  Log.d("AnalyTools", var25.toString());
               } else {
                  var25 = new StringBuilder();
                  var25.append("databyte[0]:");
                  var25.append(var1[0]);
                  Log.d("AnalyTools", var25.toString());
               }

               var14 = null;
               var24 = var14;
               var43 = var14;
               var3 = false;
               var12 = 0;
               var17 = var14;
               var40 = var14;
               var36 = var14;
            }

            waterCodeLisnter.caijishujuOnback(var3, var14, mproductid, mdeviceid, maccountid, var36, var12, var40, var17, var24, var43, var0);
         } else {
            StringBuilder var23;
            if (var13[4] == 25) {
               if (var1[0] != -128) {
                  var23 = new StringBuilder();
                  var23.append("databyte[0]:");
                  var23.append(var1[0]);
                  Log.d("AnalyTools", var23.toString());
               }
            } else if (var13[4] == 32) {
               if (var1[0] != -128) {
                  var23 = new StringBuilder();
                  var23.append("databyte[0]:");
                  var23.append(var1[0]);
                  Log.d("AnalyTools", var23.toString());
               }
            } else if (var13[4] == 33) {
               if (var1[0] == -128) {
                  var3 = true;
               } else {
                  var23 = new StringBuilder();
                  var23.append("databyte[0]:");
                  var23.append(var1[0]);
                  Log.d("AnalyTools", var23.toString());
               }

               waterCodeLisnter.xiafafeilvOnback(var3);
            } else if (var13[4] == 34) {
               if (var1[0] == -128) {
                  var3 = true;
               } else {
                  var23 = new StringBuilder();
                  var23.append("databyte[0]:");
                  var23.append(var1[0]);
                  Log.d("AnalyTools", var23.toString());
                  var3 = var4;
               }

               waterCodeLisnter.jieshufeilvOnback(var3, maccountid);
            } else {
               StringBuffer var31;
               if (var13[4] == 35) {
                  int var18;
                  String var27;
                  int var28;
                  int var33;
                  int var34;
                  label441: {
                     label440: {
                        label439: {
                           if (var1[0] == -128) {
                              if (var1 != null && var1.length == 23) {
                                 var0 = new byte[4];

                                 for(var12 = 0; var12 < var0.length; var12 = var30) {
                                    var30 = var12 + 1;
                                    var0[var12] = (byte)var1[var30];
                                 }

                                 mproductid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                 var0 = new byte[4];

                                 for(var12 = 0; var12 < var0.length; ++var12) {
                                    var0[var12] = (byte)var1[var12 + 5];
                                 }

                                 mdeviceid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                 var0 = new byte[4];

                                 for(var12 = 0; var12 < var0.length; ++var12) {
                                    var0[var12] = (byte)var1[var12 + 9];
                                 }

                                 maccountid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                 macBuffer = new byte[6];
                                 var12 = 0;

                                 while(true) {
                                    var0 = macBuffer;
                                    if (var12 >= var0.length) {
                                       tac_timeBuffer = new byte[2];
                                       var12 = 0;

                                       while(true) {
                                          var0 = tac_timeBuffer;
                                          if (var12 >= var0.length) {
                                             var0 = new byte[1];

                                             for(var12 = 0; var12 < var0.length; ++var12) {
                                                var0[var12] = (byte)var1[var12 + 19];
                                             }

                                             var12 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                             var3 = true;
                                             break label440;
                                          }

                                          var0[var12] = (byte)var1[var12 + 21];
                                          ++var12;
                                       }
                                    }

                                    var0[var12] = (byte)var1[var12 + 13];
                                    ++var12;
                                 }
                              }

                              if (var1 != null && var1.length == 28) {
                                 var0 = new byte[4];

                                 for(var12 = 0; var12 < var0.length; var12 = var30) {
                                    var30 = var12 + 1;
                                    var0[var12] = (byte)var1[var30];
                                 }

                                 mproductid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                 var0 = new byte[4];

                                 for(var12 = 0; var12 < var0.length; ++var12) {
                                    var0[var12] = (byte)var1[var12 + 5];
                                 }

                                 mdeviceid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                 var0 = new byte[4];

                                 for(var12 = 0; var12 < var0.length; ++var12) {
                                    var0[var12] = (byte)var1[var12 + 9];
                                 }

                                 maccountid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                 macBuffer = new byte[6];
                                 var12 = 0;

                                 while(true) {
                                    var0 = macBuffer;
                                    if (var12 >= var0.length) {
                                       tac_timeBuffer = new byte[2];
                                       var12 = 0;

                                       while(true) {
                                          var0 = tac_timeBuffer;
                                          if (var12 >= var0.length) {
                                             var0 = new byte[1];

                                             for(var12 = 0; var12 < var0.length; ++var12) {
                                                var0[var12] = (byte)var1[var12 + 19];
                                             }

                                             var33 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                             var26 = new byte[1];

                                             for(var12 = 0; var12 < var26.length; ++var12) {
                                                var26[var12] = (byte)var1[var12 + 23];
                                             }

                                             var34 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var26));
                                             var0 = new byte[1];

                                             for(var12 = 0; var12 < var26.length; ++var12) {
                                                var0[var12] = (byte)var1[var12 + 24];
                                             }

                                             var30 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                             var0 = new byte[1];

                                             for(var12 = 0; var12 < var0.length; ++var12) {
                                                var0[var12] = (byte)var1[var12 + 25];
                                             }

                                             var18 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                             var0 = new byte[2];

                                             for(var12 = 0; var12 < var0.length; ++var12) {
                                                var0[var12] = (byte)var1[var12 + 26];
                                             }

                                             int var19 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                             var31 = new StringBuffer();

                                             for(var12 = var5; var12 < var1.length; ++var12) {
                                                var31.append(var1[var12]);
                                             }

                                             var12 = var19;
                                             var28 = var30;
                                             var30 = var18;
                                             var27 = "";
                                             break label439;
                                          }

                                          var0[var12] = (byte)var1[var12 + 21];
                                          ++var12;
                                       }
                                    }

                                    var0[var12] = (byte)var1[var12 + 13];
                                    ++var12;
                                 }
                              }

                              if (var1 != null && var1.length == 48) {
                                 var29 = DigitalTrans.bytesToHexString(var1);
                                 var23 = new StringBuilder();
                                 var23.append("hex:=");
                                 var23.append(var29);
                                 Log.d("AnalyTools", var23.toString());
                                 var0 = new byte[1];

                                 for(var12 = 0; var12 < var0.length; ++var12) {
                                    var0[var12] = (byte)var1[var12 + 0];
                                 }

                                 var38 = new byte[4];

                                 for(var12 = 0; var12 < var38.length; var12 = var28) {
                                    var28 = var12 + 1;
                                    var38[var12] = (byte)var1[var28];
                                 }

                                 var13 = new byte[4];

                                 for(var12 = 0; var12 < var13.length; ++var12) {
                                    var13[var12] = (byte)var1[var12 + 5];
                                 }

                                 var0 = new byte[4];

                                 for(var12 = 0; var12 < var0.length; ++var12) {
                                    var0[var12] = (byte)var1[var12 + 9];
                                 }

                                 macBuffer = new byte[6];
                                 var12 = 0;

                                 while(true) {
                                    var26 = macBuffer;
                                    if (var12 >= var26.length) {
                                       var26 = new byte[1];

                                       for(var12 = 0; var12 < var26.length; ++var12) {
                                          var26[var12] = (byte)var1[var12 + 19];
                                       }

                                       StringBuilder var37 = new StringBuilder();
                                       var37.append("databyte[18]:");
                                       var37.append(var1[19]);
                                       Log.d("AnalyTools", var37.toString());
                                       byte[] var39 = new byte[4];

                                       for(var12 = 0; var12 < var39.length; ++var12) {
                                          var39[var12] = (byte)var1[var12 + 20];
                                       }

                                       tac_timeBuffer = new byte[4];
                                       var12 = 0;

                                       while(true) {
                                          var39 = tac_timeBuffer;
                                          if (var12 >= var39.length) {
                                             byte[] var44 = new byte[1];

                                             for(var12 = 0; var12 < var44.length; ++var12) {
                                                var44[var12] = (byte)var1[var12 + 28];
                                             }

                                             byte[] var20 = new byte[1];

                                             for(var12 = 0; var12 < var20.length; ++var12) {
                                                var20[var12] = (byte)var1[var12 + 29];
                                             }

                                             var42 = new byte[1];

                                             for(var12 = 0; var12 < var42.length; ++var12) {
                                                var42[var12] = (byte)var1[var12 + 30];
                                             }

                                             var39 = new byte[1];

                                             for(var12 = 0; var12 < var39.length; ++var12) {
                                                var39[var12] = (byte)var1[var12 + 31];
                                             }

                                             byte[] var21 = new byte[2];

                                             for(var12 = 0; var12 < var21.length; ++var12) {
                                                var21[var12] = (byte)var1[var12 + 32];
                                             }

                                             byte[] var22 = new byte[4];

                                             for(var12 = 0; var12 < var22.length; ++var12) {
                                                var22[var12] = (byte)var1[var12 + 34];
                                             }

                                             var22 = new byte[1];

                                             for(var12 = 0; var12 < var22.length; ++var12) {
                                                var22[var12] = (byte)var1[var12 + 38];
                                             }

                                             var22 = new byte[9];

                                             for(var12 = var6; var12 < var22.length; ++var12) {
                                                var22[var12] = (byte)var1[var12 + 39];
                                             }

                                             mproductid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var38));
                                             mdeviceid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var13));
                                             maccountid = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var0));
                                             var27 = DigitalTrans.bytesToHexString(var26);
                                             var35 = new StringBuilder();
                                             var35.append("verCode:");
                                             var35.append(DigitalTrans.bytesToHexString(var26));
                                             Log.d("AnalyTools", var35.toString());
                                             var33 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var44));
                                             var34 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var20));
                                             var28 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var42));
                                             var30 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var39));
                                             var12 = DigitalTrans.hexStringToAlgorism(DigitalTrans.bytesToHexString(var21));
                                             break label439;
                                          }

                                          var39[var12] = (byte)var1[var12 + 24];
                                          ++var12;
                                       }
                                    }

                                    var26[var12] = (byte)var1[var12 + 13];
                                    ++var12;
                                 }
                              }

                              var23 = new StringBuilder();
                              var23.append("查询设备返回数据长度错误+");
                              var23.append(var1.length);
                              Log.d("AnalyTools", var23.toString());
                           } else {
                              var23 = new StringBuilder();
                              var23.append("查询设备返回数据标识头错误databyte[0]:");
                              var23.append(var1[0]);
                              Log.d("AnalyTools", var23.toString());
                           }

                           var3 = false;
                           var12 = 0;
                           break label440;
                        }

                        var3 = true;
                        var18 = var34;
                        var34 = var12;
                        break label441;
                     }

                     var27 = "";
                     var18 = 0;
                     var28 = 0;
                     var30 = 0;
                     var34 = 0;
                     var33 = var12;
                  }

                  WaterCodeListener var32 = waterCodeLisnter;
                  if (var32 != null) {
                     var32.chaxueNewshebeiOnback(var3, var33, mdeviceid, mproductid, maccountid, macBuffer, tac_timeBuffer, var18, var28, var30, var34, var27);
                  }
               } else if (var13[4] == -6) {
                  if (var1[0] == -128) {
                     var3 = true;
                  } else {
                     var23 = new StringBuilder();
                     var23.append("databyte[0]:");
                     var23.append(var1[0]);
                     Log.d("AnalyTools", var23.toString());
                     var3 = var7;
                  }

                  waterCodeLisnter.fanhuicunchuOnback(var3);
               } else if (var13[4] == 24) {
                  var31 = new StringBuffer();

                  for(var12 = 0; var12 < var1.length; ++var12) {
                     var31.append(var1[var12]);
                  }

                  var25 = new StringBuilder();
                  var25.append("sb:");
                  var25.append(var31);
                  Log.d("AnalyTools", var25.toString());
                  byte var45 = var1[0];
               } else if (var13[4] == 49) {
                  var2 = new StringBuffer();

                  for(var12 = 0; var12 < var13.length; ++var12) {
                     var2.append(var13[var12]);
                  }

                  StringBuffer var41 = new StringBuffer();

                  for(var12 = 0; var12 < var1.length; ++var12) {
                     var41.append(var1[var12]);
                  }

                  var15 = new StringBuilder();
                  var15.append("sb1:");
                  var15.append(var2.toString());
                  Log.d("AnalyTools", var15.toString());
                  var25 = new StringBuilder();
                  var25.append("sb2:");
                  var25.append(var41.toString());
                  Log.d("AnalyTools", var25.toString());
                  var3 = var8;
                  if (var1[0] == -128) {
                     var3 = true;
                  }

                  waterCodeLisnter.startDeal(var3, var0);
               } else if (var13[4] == 50) {
                  var3 = var9;
                  if (var1[0] == -128) {
                     var3 = true;
                  }

                  waterCodeLisnter.stopDeal(var3);
               } else {
                  var31 = new StringBuffer();

                  for(var12 = var10; var12 < var13.length; ++var12) {
                     var31.append(var1[var12]);
                  }

                  var25 = new StringBuilder();
                  var25.append("sb:");
                  var25.append(var31);
                  Log.d("AnalyTools", var25.toString());
               }
            }
         }
      }

   }

   public static final byte[] handleResult2(String var0) {
      Log.d("Toolsutils", var0);
      byte[] var1 = DigitalTrans.hexStringToBytes(var0);
      byte[] var7;
      if (var1 != null && var1.length >= 9) {
         int var2 = var1[2] - 3;
         if (var2 > 0) {
            byte[] var3 = new byte[var2];
            var2 = 0;

            int var4;
            for(var4 = 0; var2 < var3.length; ++var2) {
               int var5 = var2 + 6;
               var3[var2] = (byte)var1[var5];
               var4 += var1[var5];
            }

            int var6 = Math.abs(var4);
            byte var9 = var1[3];
            byte var8 = var1[4];
            byte var10 = var1[5];
            var7 = var3;
            if (var3.length > 0) {
               var7 = var3;
               if (var6 + var9 + var8 + var10 == var1[var1.length - 2]) {
                  if (var3[0] == -128) {
                     Log.d("Toolsutils", "R.string.bluetooth_callback_success:");
                     var7 = var3;
                  } else {
                     Log.d("Toolsutils", "R.string.bluetooth_callback_fail:");
                     var7 = var3;
                  }
               }
            }
         } else {
            var7 = new byte[0];
         }
      } else {
         var7 = null;
      }

      return var7;
   }

   public static void setWaterCodeLisnter(WaterCodeListener var0) {
      waterCodeLisnter = var0;
   }
}
