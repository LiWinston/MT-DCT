/**
 * @Author: 1378156 Yongchun Li
 */

package client;

import java.util.Stack;
import java.util.function.Predicate;

public class SymbolMatch {
    static Predicate<Character> keepCharacter = c -> Character.isLetter(c) || c == ' ' || "+=,.!-_$#@^&*?\"".indexOf(c) != -1;
    public static void main(String[] args) {
        System.out.println(checkParenthesesMatchandDeleteIllegal("dfsds[][[]]]")); // 示例调用
    }

    public static String checkParenthesesMatchandDeleteIllegal(String target) {
        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder();

        // 遍历字符串中的每个字符
        for (char c : target.toCharArray()) {
            // 如果是字母文字，直接添加到结果中
            if (keepCharacter.test(c)) {
                result.append(c);
            }
            // 如果是左括号，入栈
            else if (c == '(' || c == '[' || c == '{'|| c == '<') {
                stack.push(c);
                result.append(c);
            }
            // 如果是右括号
            else if (c == ')' || c == ']' || c == '}' || c == '>') {
                // 如果栈为空，或者栈顶元素与当前右括号不匹配，则将当前右括号视为非法，并继续下一个字符
                if (stack.isEmpty() || !isMatching(stack.peek(), c)) {
                    continue;
                }
                // 否则，出栈
                else {
                    stack.pop();
                    result.append(c);
                }
            }
        }

        // 删除栈中剩余的左括号
        while (!stack.isEmpty()) {
            result.deleteCharAt(result.lastIndexOf(stack.pop().toString()));
        }

        return result.toString();
    }

    // 判断左右括号是否匹配
    private static boolean isMatching(char left, char right) {
        return (left == '(' && right == ')') ||
                (left == '[' && right == ']') ||
                (left == '{' && right == '}') ||
                (left == '<' && right == '>');
    }
}
