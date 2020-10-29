package com.magna.moldingtools.Controler;

public interface CommandState {
    int COMMAND_STATE_BEGIN = 0;
    int COMMAND_STATE_QUERY = 1;
    int COMMAND_STATE_SET = 2;
    int COMMAND_STATE_VERIFY = 3;
    int COMMAND_STATE_END = 4;

    void a(String var1);

    void b(String var1);

    void c(String var1);

    void d(String var1);

    void e(String var1);
}
