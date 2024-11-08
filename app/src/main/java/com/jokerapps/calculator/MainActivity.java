package com.jokerapps.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

public class MainActivity extends AppCompatActivity {
    private TextView display;
    private GridLayout standardButtons, scientificButtons;
    private boolean isScientificMode = true;
    private final StringBuilder expression = new StringBuilder();
    private boolean isError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        standardButtons = findViewById(R.id.standard_buttons);
        scientificButtons = findViewById(R.id.scientific_buttons);

        isScientificMode = false;
        scientificButtons.setVisibility(View.GONE);

        setupStandardButtons();
        setupScientificButtons();
        updateDisplay();
    }

    private void setupStandardButtons() {
        findViewById(R.id.button_clear).setOnClickListener(view -> clear());
        findViewById(R.id.button_delete).setOnClickListener(view -> deleteLast());
        findViewById(R.id.button_equals).setOnClickListener(view -> calculate());

        findViewById(R.id.button_0).setOnClickListener(view -> appendToExpression("0"));
        findViewById(R.id.button_1).setOnClickListener(view -> appendToExpression("1"));
        findViewById(R.id.button_2).setOnClickListener(view -> appendToExpression("2"));
        findViewById(R.id.button_3).setOnClickListener(view -> appendToExpression("3"));
        findViewById(R.id.button_4).setOnClickListener(view -> appendToExpression("4"));
        findViewById(R.id.button_5).setOnClickListener(view -> appendToExpression("5"));
        findViewById(R.id.button_6).setOnClickListener(view -> appendToExpression("6"));
        findViewById(R.id.button_7).setOnClickListener(view -> appendToExpression("7"));
        findViewById(R.id.button_8).setOnClickListener(view -> appendToExpression("8"));
        findViewById(R.id.button_9).setOnClickListener(view -> appendToExpression("9"));

        findViewById(R.id.button_add).setOnClickListener(view -> appendToExpression("+"));
        findViewById(R.id.button_subtract).setOnClickListener(view -> appendToExpression("-"));
        findViewById(R.id.button_multiply).setOnClickListener(view -> appendToExpression("*"));
        findViewById(R.id.button_divide).setOnClickListener(view -> appendToExpression("/"));
        findViewById(R.id.button_modulus).setOnClickListener(view -> appendToExpression("%"));
        findViewById(R.id.button_dot).setOnClickListener(view -> appendToExpression("."));

        findViewById(R.id.button_toggle_scientific).setOnClickListener(view -> toggleScientificMode());
    }

    private void setupScientificButtons() {
        findViewById(R.id.button_sin).setOnClickListener(view -> appendToExpression("sin("));
        findViewById(R.id.button_cos).setOnClickListener(view -> appendToExpression("cos("));
        findViewById(R.id.button_tan).setOnClickListener(view -> appendToExpression("tan("));
        findViewById(R.id.button_sec).setOnClickListener(view -> appendToExpression("sec("));
        findViewById(R.id.button_cosec).setOnClickListener(view -> appendToExpression("cosec("));
        findViewById(R.id.button_cot).setOnClickListener(view -> appendToExpression("cot("));
        findViewById(R.id.button_sqrt).setOnClickListener(view -> appendToExpression("sqrt("));
        findViewById(R.id.button_power).setOnClickListener(view -> appendToExpression("^"));
        findViewById(R.id.button_pi).setOnClickListener(view -> appendToExpression("π"));
        findViewById(R.id.button_log).setOnClickListener(view -> appendToExpression("log("));
        findViewById(R.id.button_ln).setOnClickListener(view -> appendToExpression("ln("));
        findViewById(R.id.button_factorial).setOnClickListener(view -> appendToExpression("!"));
        findViewById(R.id.button_open_parenthesis).setOnClickListener(view -> appendToExpression("("));
        findViewById(R.id.button_close_parenthesis).setOnClickListener(view -> appendToExpression(")"));
        findViewById(R.id.button_exponent).setOnClickListener(view -> appendToExpression("e"));
    }

    private void appendToExpression(String value) {
        if (isError) {
            clear();
        }
        expression.append(value);
        updateDisplay();
    }

    private void clear() {
        expression.setLength(0);
        isError = false;
        updateDisplay();
    }

    private void deleteLast() {
        if (isError) {
            clear();
        } else {
            int length = expression.length();
            if (length > 0) {
                expression.deleteCharAt(length - 1);
            }
            updateDisplay();
        }
    }

    private void toggleScientificMode() {
        isScientificMode = !isScientificMode;
        scientificButtons.setVisibility(isScientificMode ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.button_toggle_scientific)).setText(isScientificMode ? R.string.scientific_mode : R.string.standard_mode);
    }

    private void updateDisplay() {
        display.setText(expression.toString().isEmpty() ? getString(R.string.text_area_nothing) : expression.toString());
    }

    private void calculate() {
        String result = evaluateExpression(expression.toString());
        display.setText(result);
        expression.setLength(0);

        if (result.equals("Error")) {
            isError = true;
        } else {
            expression.append(result);
            isError = false;
        }
    }

    private String evaluateExpression(String expr) {
        try {
            expr = expr.replace("π", String.valueOf(Math.PI))
                    .replace("e", String.valueOf(Math.E));

            Operator factorial = new Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
                @Override
                public double apply(double... args) {
                    int result = 1;
                    for (int i = 1; i <= (int) args[0]; i++) {
                        result *= i;
                    }
                    return result;
                }
            };

            Function sin = new Function("sin", 1) {
                @Override
                public double apply(double... args) {
                    return Math.sin(args[0]);
                }
            };
            Function cos = new Function("cos", 1) {
                @Override
                public double apply(double... args) {
                    return Math.cos(args[0]);
                }
            };
            Function tan = new Function("tan", 1) {
                @Override
                public double apply(double... args) {
                    return Math.tan(args[0]);
                }
            };
            Function log = new Function("log", 1) {
                @Override
                public double apply(double... args) {
                    return Math.log10(args[0]);
                }
            };
            Function ln = new Function("ln", 1) {
                @Override
                public double apply(double... args) {
                    return Math.log(args[0]);
                }
            };
            Function sqrt = new Function("sqrt", 1) {
                @Override
                public double apply(double... args) {
                    return Math.sqrt(args[0]);
                }
            };
            Function sec = new Function("sec", 1) {
                @Override
                public double apply(double... args) {
                    return 1.0 / Math.cos(args[0]);
                }
            };
            Function cosec = new Function("cosec", 1) {
                @Override
                public double apply(double... args) {
                    return 1.0 / Math.sin(args[0]);
                }
            };
            Function cot = new Function("cot", 1) {
                @Override
                public double apply(double... args) {
                    return 1.0 / Math.tan(args[0]);
                }
            };

            Expression expression = new ExpressionBuilder(expr)
                    .operator(factorial)
                    .function(sin)
                    .function(cos)
                    .function(tan)
                    .function(log)
                    .function(ln)
                    .function(sqrt)
                    .function(sec)
                    .function(cosec)
                    .function(cot)
                    .build();

            double result = expression.evaluate();
            return String.valueOf(result);
        } catch (ArithmeticException | IllegalArgumentException e) {
            return String.valueOf(R.string.error_text);
        }
    }

    public GridLayout getStandardButtons() {
        return standardButtons;
    }

    public void setStandardButtons(GridLayout standardButtons) {
        this.standardButtons = standardButtons;
    }
}