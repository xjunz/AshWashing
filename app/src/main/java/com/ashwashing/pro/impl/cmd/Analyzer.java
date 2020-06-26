//
// Decompiled by FernFlower - 3216ms
//
package com.ashwashing.pro.impl.cmd;

public class Analyzer {
    private static byte[] mac;
    private static int account_id;
    private static int device_id;
    private static int product_id;
    private static byte[] tac;
    private static Listener sListener;

    private static byte[] analyzeOrigin(byte[] var0) {
        String var1 = Processor.bytes2hex(var0);
        if (var1.startsWith("23") && var1.endsWith("0a")) {
            var1 = var1.substring(2, var1.length() - 2);
        } else {
            var1 = null;
        }

        return handleResult2(Processor.ascii2string(var1));
    }

    private static byte[] analyzeOrigin2(byte[] var0) {
        String var1 = Processor.bytes2hex(var0);
        if (var1.startsWith("23") && var1.endsWith("0a")) {
            var1 = var1.substring(2, var1.length() - 2);
        } else {
            var1 = null;
        }

        Processor.ascii2string(var1);
        return Processor.hex2byte(Processor.ascii2string(var1));
    }

    public static void analyze(byte[] var0) {
        byte[] var18 = analyzeOrigin(var0);
        StringBuffer var13 = new StringBuffer();
        boolean success = false;
        byte var3 = 0;
        byte var2 = 0;
        byte var5 = 0;
        byte var4 = 0;

        int var1;
        for (var1 = 0; var1 < var18.length; ++var1) {
            var13.append(var18[var1]);
        }

        byte[] var14 = analyzeOrigin2(var0);
        if (var14 != null) {
            StringBuilder var16;
            int var24;
            StringBuilder var30;
            byte[] var31;
            StringBuilder var32;
            String var37;
            byte[] var43;
            if (var14[4] == -5) {
                String var15;
                String var17;
                String var34;
                String var35;
                String var41;
                String var44;
                label547:
                {
                    if (var18[0] == -128) {
                        if (var18.length == 42) {
                            var13 = new StringBuffer();

                            for (var1 = 0; var1 < var18.length; ++var1) {
                                var13.append(var18[var1]);
                            }

                            var31 = new byte[6];

                            for (var1 = 0; var1 < var31.length; var1 = var24) {
                                var24 = var1 + 1;
                                var31[var1] = var18[var24];
                            }

                            var17 = Processor.bytes2hex(var31);
                            var14 = new byte[4];

                            for (var1 = 0; var1 < var14.length; ++var1) {
                                var14[var1] = var18[var1 + 7];
                            }

                            var30 = new StringBuilder();
                            var30.append(Processor.hex2decimal(Processor.bytes2hex(var14)));
                            var30.append("");
                            // var30.toString();
                            product_id = Processor.hex2decimal(Processor.bytes2hex(var14));
                            var14 = new byte[4];

                            for (var1 = 0; var1 < var14.length; ++var1) {
                                var14[var1] = var18[var1 + 11];
                            }

                            var30 = new StringBuilder();
                            var30.append(Processor.hex2decimal(Processor.bytes2hex(var14)));
                            var30.append("");
                            //var30.toString();
                            device_id = Processor.hex2decimal(Processor.bytes2hex(var14));
                            var31 = new byte[4];

                            for (var1 = 0; var1 < var31.length; ++var1) {
                                var31[var1] = var18[var1 + 15];
                            }

                            var32 = new StringBuilder();
                            var32.append(Processor.hex2decimal(Processor.bytes2hex(var31)));
                            var32.append("");
                            //var32.toString();
                            account_id = Processor.hex2decimal(Processor.bytes2hex(var31));
                            var14 = new byte[1];

                            for (var1 = 0; var1 < var14.length; ++var1) {
                                var14[var1] = var18[var1 + 19];
                            }

                            var30 = new StringBuilder();
                            var30.append(Processor.hex2decimal(Processor.bytes2hex(var14)));
                            var30.append("");
                            var15 = var30.toString();
                            var31 = new byte[4];

                            for (var1 = 0; var1 < var31.length; ++var1) {
                                var31[var1] = var18[var1 + 20];
                            }

                            var32 = new StringBuilder();
                            var32.append(Processor.hex2decimal(Processor.bytes2hex(var31)));
                            var32.append("");
                            // var32.toString();
                            var24 = Processor.hex2decimal(Processor.bytes2hex(var31));
                            var31 = new byte[4];

                            for (var1 = 0; var1 < var31.length; ++var1) {
                                var31[var1] = var18[var1 + 24];
                            }

                            var32 = new StringBuilder();
                            var32.append(Processor.hex2decimal(Processor.bytes2hex(var31)));
                            var32.append("");
                            var34 = var32.toString();
                            var31 = new byte[4];

                            for (var1 = 0; var1 < var31.length; ++var1) {
                                var31[var1] = var18[var1 + 28];
                            }

                            var16 = new StringBuilder();
                            var16.append(Processor.hex2decimal(Processor.bytes2hex(var31)));
                            var16.append("");
                            var35 = var16.toString();
                            var31 = new byte[4];

                            for (var1 = 0; var1 < var31.length; ++var1) {
                                var31[var1] = var18[var1 + 32];
                            }

                            var37 = Processor.hex2decimal(Processor.bytes2hex(var31)) + "";
                            var43 = new byte[6];

                            for (var1 = var4; var1 < var43.length; ++var1) {
                                var43[var1] = var18[var1 + 36];
                            }

                            var44 = Processor.bytes2hex(var43);
                            var1 = var24;
                            success = true;
                            var41 = var37;
                            break label547;
                        }
                    }

                    var17 = null;
                    var34 = null;
                    var44 = null;
                    success = false;
                    var1 = 0;
                    var41 = null;
                    var35 = null;
                    var15 = null;
                }
                sListener.afterCollectDeviceDatum(success, var17, product_id, device_id, account_id, var15, var1, var34, var35, var41, var44, var0);
            } else {
                //StringBuilder var23;
                if (var14[4] == 33) {
                    if (var18[0] == -128) {
                        success = true;
                    }
                    sListener.afterDispatchFeeRate(success);
                } else if (var14[4] == 34) {
                    success = var18[0] == -128;
                    sListener.afterEndSpray(success, account_id);
                } else {
                    StringBuffer var29;
                    if (var14[4] == 35) {
                        int var6;
                        String var25;
                        int var26;
                        int var27;
                        int deviceStatus;
                        label441:
                        {
                            label440:
                            {
                                label439:
                                {
                                    if (var18[0] == -128) {
                                        if (var18.length == 23) {
                                            var0 = new byte[4];

                                            for (var1 = 0; var1 < var0.length; var1 = var24) {
                                                var24 = var1 + 1;
                                                var0[var1] = var18[var24];
                                            }

                                            product_id = Processor.hex2decimal(Processor.bytes2hex(var0));
                                            var0 = new byte[4];

                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                var0[var1] = var18[var1 + 5];
                                            }

                                            device_id = Processor.hex2decimal(Processor.bytes2hex(var0));
                                            var0 = new byte[4];

                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                var0[var1] = var18[var1 + 9];
                                            }

                                            account_id = Processor.hex2decimal(Processor.bytes2hex(var0));
                                            mac = new byte[6];
                                            var1 = 0;

                                            while (true) {
                                                var0 = mac;
                                                if (var1 >= var0.length) {
                                                    tac = new byte[2];
                                                    var1 = 0;

                                                    while (true) {
                                                        var0 = tac;
                                                        if (var1 >= var0.length) {
                                                            var0 = new byte[1];

                                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                                var0[var1] = var18[var1 + 19];
                                                            }

                                                            var1 = Processor.hex2decimal(Processor.bytes2hex(var0));
                                                            success = true;
                                                            break label440;
                                                        }

                                                        var0[var1] = var18[var1 + 21];
                                                        ++var1;
                                                    }
                                                }

                                                var0[var1] = var18[var1 + 13];
                                                ++var1;
                                            }
                                        }

                                        if (var18.length == 28) {
                                            var0 = new byte[4];

                                            for (var1 = 0; var1 < var0.length; var1 = var24) {
                                                var24 = var1 + 1;
                                                var0[var1] = var18[var24];
                                            }

                                            product_id = Processor.hex2decimal(Processor.bytes2hex(var0));
                                            var0 = new byte[4];

                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                var0[var1] = var18[var1 + 5];
                                            }

                                            device_id = Processor.hex2decimal(Processor.bytes2hex(var0));
                                            var0 = new byte[4];

                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                var0[var1] = var18[var1 + 9];
                                            }

                                            account_id = Processor.hex2decimal(Processor.bytes2hex(var0));
                                            mac = new byte[6];
                                            var1 = 0;

                                            while (true) {
                                                var0 = mac;
                                                if (var1 >= var0.length) {
                                                    tac = new byte[2];
                                                    var1 = 0;

                                                    while (true) {
                                                        var0 = tac;
                                                        if (var1 >= var0.length) {
                                                            var0 = new byte[1];

                                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                                var0[var1] = var18[var1 + 19];
                                                            }

                                                            deviceStatus = Processor.hex2decimal(Processor.bytes2hex(var0));
                                                            var0 = new byte[1];

                                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                                var0[var1] = var18[var1 + 23];
                                                            }

                                                            var27 = Processor.hex2decimal(Processor.bytes2hex(var0));
                                                            var31 = new byte[1];

                                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                                var31[var1] = var18[var1 + 24];
                                                            }

                                                            var24 = Processor.hex2decimal(Processor.bytes2hex(var31));
                                                            var0 = new byte[1];

                                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                                var0[var1] = var18[var1 + 25];
                                                            }

                                                            var6 = Processor.hex2decimal(Processor.bytes2hex(var0));
                                                            var0 = new byte[2];

                                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                                var0[var1] = var18[var1 + 26];
                                                            }

                                                            int var7 = Processor.hex2decimal(Processor.bytes2hex(var0));
                                                            var29 = new StringBuffer();

                                                            for (var1 = var3; var1 < var18.length; ++var1) {
                                                                var29.append(var18[var1]);
                                                            }

                                                            var1 = var7;
                                                            var26 = var24;
                                                            var24 = var6;
                                                            var25 = "";
                                                            break label439;
                                                        }

                                                        var0[var1] = var18[var1 + 21];
                                                        ++var1;
                                                    }
                                                }

                                                var0[var1] = var18[var1 + 13];
                                                ++var1;
                                            }
                                        }

                                        if (var18.length == 48) {
                                            var0 = new byte[1];

                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                var0[var1] = var18[var1];
                                            }

                                            var14 = new byte[4];

                                            for (var1 = 0; var1 < var14.length; var1 = var26) {
                                                var26 = var1 + 1;
                                                var14[var1] = var18[var26];
                                            }

                                            var0 = new byte[4];

                                            for (var1 = 0; var1 < var0.length; ++var1) {
                                                var0[var1] = var18[var1 + 5];
                                            }

                                            byte[] var33 = new byte[4];

                                            for (var1 = 0; var1 < var33.length; ++var1) {
                                                var33[var1] = var18[var1 + 9];
                                            }

                                            mac = new byte[6];
                                            var1 = 0;

                                            while (true) {
                                                var31 = mac;
                                                if (var1 >= var31.length) {
                                                    var31 = new byte[1];

                                                    for (var1 = 0; var1 < var31.length; ++var1) {
                                                        var31[var1] = var18[var1 + 19];
                                                    }

                                                    var16 = new StringBuilder();
                                                    var16.append("databyte[18]:");
                                                    var16.append(var18[19]);

                                                    byte[] var38 = new byte[4];

                                                    for (var1 = 0; var1 < var38.length; ++var1) {
                                                        var38[var1] = var18[var1 + 20];
                                                    }

                                                    tac = new byte[4];
                                                    var1 = 0;

                                                    while (true) {
                                                        var38 = tac;
                                                        if (var1 >= var38.length) {
                                                            var38 = new byte[1];

                                                            for (var1 = 0; var1 < var38.length; ++var1) {
                                                                var38[var1] = var18[var1 + 28];
                                                            }

                                                            byte[] var40 = new byte[1];

                                                            for (var1 = 0; var1 < var40.length; ++var1) {
                                                                var40[var1] = var18[var1 + 29];
                                                            }

                                                            byte[] var20 = new byte[1];

                                                            for (var1 = 0; var1 < var20.length; ++var1) {
                                                                var20[var1] = var18[var1 + 30];
                                                            }

                                                            var43 = new byte[1];

                                                            for (var1 = 0; var1 < var43.length; ++var1) {
                                                                var43[var1] = var18[var1 + 31];
                                                            }

                                                            byte[] var21 = new byte[2];

                                                            for (var1 = 0; var1 < var21.length; ++var1) {
                                                                var21[var1] = var18[var1 + 32];
                                                            }

                                                            byte[] var22 = new byte[4];

                                                            for (var1 = 0; var1 < var22.length; ++var1) {
                                                                var22[var1] = var18[var1 + 34];
                                                            }

                                                            var22 = new byte[1];

                                                            for (var1 = 0; var1 < var22.length; ++var1) {
                                                                var22[var1] = var18[var1 + 38];
                                                            }

                                                            var22 = new byte[9];

                                                            for (var1 = var2; var1 < var22.length; ++var1) {
                                                                var22[var1] = var18[var1 + 39];
                                                            }

                                                            product_id = Processor.hex2decimal(Processor.bytes2hex(var14));
                                                            device_id = Processor.hex2decimal(Processor.bytes2hex(var0));
                                                            account_id = Processor.hex2decimal(Processor.bytes2hex(var33));
                                                            var25 = Processor.bytes2hex(var31);
                                                            var32 = new StringBuilder();
                                                            var32.append("verCode:");
                                                            var32.append(Processor.bytes2hex(var31));
                                                            deviceStatus = Processor.hex2decimal(Processor.bytes2hex(var38));
                                                            var27 = Processor.hex2decimal(Processor.bytes2hex(var40));
                                                            var26 = Processor.hex2decimal(Processor.bytes2hex(var20));
                                                            var24 = Processor.hex2decimal(Processor.bytes2hex(var43));
                                                            var1 = Processor.hex2decimal(Processor.bytes2hex(var21));
                                                            break label439;
                                                        }

                                                        var38[var1] = var18[var1 + 24];
                                                        ++var1;
                                                    }
                                                }

                                                var31[var1] = var18[var1 + 13];
                                                ++var1;
                                            }
                                        }

                                    }
                                    success = false;
                                    var1 = 0;
                                    break label440;
                                }

                                success = true;
                                var6 = var1;
                                break label441;
                            }

                            var25 = "";
                            var27 = 0;
                            var26 = 0;
                            var24 = 0;
                            var6 = 0;
                            deviceStatus = var1;
                        }

                        if (sListener != null) {
                            sListener.afterAwakenDevice(success, deviceStatus, device_id, product_id, account_id, mac, tac, var27, var26, var24, var6, var25);
                        }
                    } else if (var14[4] == -6) {
                        success = var18[0] == -128;
                        sListener.afterStoreSpray(success);
                    } else if (var14[4] == 24) {
                        var29 = new StringBuffer();

                        for (var1 = 0; var1 < var18.length; ++var1) {
                            var29.append(var18[var1]);
                        }

                        var30 = new StringBuilder();
                        var30.append("sb:");
                        var30.append(var29);
                        //  Log.d("AnalyTools", var30.toString());
                        //  byte var45 = var18[0];
                    } else if (var14[4] == 49) {
                        var13 = new StringBuffer();

                        for (var1 = 0; var1 < var14.length; ++var1) {
                            var13.append(var14[var1]);
                        }

                        StringBuilder var39 = new StringBuilder();

                        for (var1 = 0; var1 < var18.length; ++var1) {
                            var39.append(var18[var1]);
                        }

                /*  String var36 = "sb1:" +
                          var13.toString();
                  //Log.d("AnalyTools", var36);*/
                        var30 = new StringBuilder();
                        var30.append("sb2:");
                        var30.append(var39.toString());
                        // Log.d("AnalyTools", var30.toString());
                        success = var18[0] == -128;
                        sListener.startDeal(success, var0);
                    } else if (var14[4] == 50) {
                        success = var18[0] == -128;

                        sListener.stopDeal(success);
                    } else {
                        var29 = new StringBuffer();

                        for (var1 = var5; var1 < var14.length; ++var1) {
                            var29.append(var18[var1]);
                        }

                        var30 = new StringBuilder();
                        var30.append("sb:");
                        var30.append(var29);
                        // Log.d("AnalyTools", var30.toString());
                    }
                }
            }
        }

    }

    private static byte[] handleResult2(String var0) {
        //Log.d("Toolsutils", var0);
        byte[] var6 = Processor.hex2bytes(var0);
        byte[] var7;
        if (var6 != null && var6.length >= 9) {
            int var1 = var6[2] - 3;
            if (var1 > 0) {
                byte[] var5 = new byte[var1];
                var1 = 0;
                for (; var1 < var5.length; ++var1) {
                    int var3 = var1 + 6;
                    var5[var1] = var6[var3];
                }
                var7 = var5;
            } else {
                var7 = new byte[0];
            }
        } else {
            var7 = null;
        }
        return var7;
    }

    public static void setListener(Listener var0) {
        sListener = var0;
    }
}
