package org.example;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Stack;

public class RMIClient extends Thread{

    public void run(){}
    public void calcular(String expresion) {
        try {
            Registry aritmeticasRegistry = LocateRegistry.getRegistry(Constants.host, Constants.RMIA_PORT);
            AritmeticasInterface aritmeticasServer = (AritmeticasInterface) aritmeticasRegistry.lookup("Aritmeticas");

            Registry trigonometricasRegistry = LocateRegistry.getRegistry(Constants.host, Constants.RMIT_PORT);
            TrigonometricasInterface trigonometricasServer = (TrigonometricasInterface) trigonometricasRegistry.lookup("Trigonometricas");


            //String expression = "(2+3)*sin(90)";
            double result = evaluateExpression(expresion, aritmeticasServer, trigonometricasServer);

            System.out.println("Resultado: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double evaluateExpression(String expression, AritmeticasInterface aritmeticasServer, TrigonometricasInterface trigonometricasServer) throws RemoteException {
        Stack<Double> numbers = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c)) {
                StringBuilder numBuilder = new StringBuilder();
                numBuilder.append(c);

                while (i + 1 < expression.length() && Character.isDigit(expression.charAt(i + 1))) {
                    numBuilder.append(expression.charAt(i + 1));
                    i++;
                }

                double num = Double.parseDouble(numBuilder.toString());
                numbers.push(num);
            } else if (c == '(') {
                operators.push("(");
            } else if (c == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    String operator = operators.pop();
                    if (operator.equals("sin") || operator.equals("cos") || operator.equals("tan") ||
                        operator.equals("cot") || operator.equals("csc") || operator.equals("sec") ||
                        operator.equals("log") || operator.equals("sqrt")) {
                        double num = numbers.pop();
                        double result;
                        if(operator.equals("sqrt")) result = applyOperator(num,2, operator, aritmeticasServer);
                        else result = applyOperator(num, operator, trigonometricasServer);
                        numbers.push(result);
                    } else {
                        double num2 = numbers.pop();
                        double num1 = numbers.pop();
                        double result = applyOperator(num1, num2, operator, aritmeticasServer);
                        numbers.push(result);
                    }
                }

                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop(); // Remove '(' from stack
                }
            } else if (isOperator(String.valueOf(c))) {
                String operator = String.valueOf(c);
                while (!operators.isEmpty() && hasPrecedence(operator, operators.peek())) {
                    String topOperator = operators.pop();
                    if (topOperator.equals("sin") || topOperator.equals("cos") || topOperator.equals("tan") ||
                        topOperator.equals("cot") || topOperator.equals("csc") || topOperator.equals("sec") ||
                        topOperator.equals("log") || topOperator.equals("sqrt")) {
                        double num = numbers.pop();

                        double result;
                        if(topOperator.equals("sqrt")) result = applyOperator(num,2, topOperator, aritmeticasServer);
                        else result = applyOperator(num, topOperator, trigonometricasServer);
                        numbers.push(result);
                    }
                    else {
                        double num2 = numbers.pop();
                        double num1 = numbers.pop();
                        double result = applyOperator(num1, num2, topOperator, aritmeticasServer);
                        numbers.push(result);
                    }
                }

                operators.push(operator);
            } else if (Character.isLetter(c)) {
                StringBuilder funcBuilder = new StringBuilder();
                funcBuilder.append(c);

                while (i + 1 < expression.length() && Character.isLetter(expression.charAt(i + 1))) {
                    funcBuilder.append(expression.charAt(i + 1));
                    i++;
                }

                String function = funcBuilder.toString();
                operators.push(function);
            }
        }

        while (!operators.isEmpty()) {
            String operator = operators.pop();
            if (operator.equals("sin") || operator.equals("cos") || operator.equals("tan") ||
            operator.equals("cot") || operator.equals("csc") || operator.equals("sec") ||
                    operator.equals("log") || operator.equals("sqrt")) {
                double num = numbers.pop();
                double result;
                if(operator.equals("sqrt")) result = applyOperator(num,2, operator, aritmeticasServer);
                else result = applyOperator(num, operator, trigonometricasServer);
                numbers.push(result);
            } else {
                double num2 = numbers.pop();
                double num1 = numbers.pop();
                double result = applyOperator(num1, num2, operator, aritmeticasServer);
                numbers.push(result);
            }
        }

        return numbers.pop();
    }

    private double applyOperator(double num, String operator, TrigonometricasInterface trigonometricasServer) throws RemoteException {
        if (operator.equals("sin")) {
            return trigonometricasServer.sin(num);
        } else if (operator.equals("cos")) {
            return trigonometricasServer.cos(num);
        } else if (operator.equals("tan")) {
            return trigonometricasServer.tan(num);
        } else if (operator.equals("cot")) {
            return  trigonometricasServer.cot(num);
        } else if (operator.equals("sec")) {
            return  trigonometricasServer.sec(num);
        } else if (operator.equals("csc")) {
            return  trigonometricasServer.csc(num);
        } else if (operator.equals("log")) {
            return  trigonometricasServer.log(num);
        }

        return 0.0; // Default case
    }

    private double applyOperator(double num1, double num2, String operator, AritmeticasInterface aritmeticasServer) throws RemoteException {
        if (operator.equals("+")) {
            return aritmeticasServer.suma(num1, num2);
        } else if (operator.equals("-")) {
            return aritmeticasServer.resta(num1, num2);
        } else if (operator.equals("*")) {
            return aritmeticasServer.multiplicacion(num1, num2);
        } else if (operator.equals("/")) {
            return aritmeticasServer.division(num1, num2);
        } else if (operator.equals("^")){
            return aritmeticasServer.exponenciacion(num1,num2);
        } else if (operator.equals("sqrt")){
            return aritmeticasServer.raiz(num1);
        }

        return 0.0; // Default case
    }

    private boolean isOperator(String operator) {
        return operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/") || operator.equals("^")
                || operator.equals("sin") || operator.equals("cos") || operator.equals("tan")  ||
                operator.equals("cot") || operator.equals("csc") || operator.equals("sec") ||
                operator.equals("log") || operator.equals("sqrt");
    }

    private boolean hasPrecedence(String op1, String op2) {
        if (op2.equals("(") || op2.equals(")")) {
            return false;
        }

        if ((op1.equals("^") && !op2.equals("^")) || ((op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-")))) {
            return false;
        }

        return true;
    }

}