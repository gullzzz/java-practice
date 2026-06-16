package com.practice.arrays;

import java.util.Arrays;

/**
 * 阶段4：数组
 * 涵盖：数组声明与初始化、遍历、二维数组、Arrays工具类、常见算法
 */
public class ArraysDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 数组 ==========\n");

        arrayBasics();
        arrayTraversal();
        twoDimensionalArray();
        arraysUtil();
        commonAlgorithms();
    }

    static void arrayBasics() {
        System.out.println("--- 1. 数组的声明与初始化 ---");

        // 方式1：先声明，再分配空间
        int[] arr1 = new int[5];
        arr1[0] = 10;
        arr1[1] = 20;
        System.out.println("arr1[0]=" + arr1[0] + ", arr1[1]=" + arr1[1] + ", arr1[4]=" + arr1[4] + " (默认值0)");

        // 方式2：声明+初始化
        int[] arr2 = {1, 2, 3, 4, 5};
        String[] names = {"张三", "李四", "王五"};

        System.out.println("arr2长度: " + arr2.length);
        System.out.println("names[1]: " + names[1]);

        // 数组长度固定，不能改变！
        // arr2[5] = 6; // ❌ ArrayIndexOutOfBoundsException

        System.out.println();
    }

    static void arrayTraversal() {
        System.out.println("--- 2. 数组遍历 ---");

        int[] scores = {85, 92, 78, 95, 88};

        // 方式1：普通 for 循环（有索引）
        System.out.print("for: ");
        for (int i = 0; i < scores.length; i++) {
            System.out.print(scores[i] + " ");
        }
        System.out.println();

        // 方式2：增强 for（for-each，没有索引）
        System.out.print("for-each: ");
        for (int s : scores) {
            System.out.print(s + " ");
        }
        System.out.println();

        // for-each 不能修改数组元素，只能读取
        int sum = 0;
        for (int s : scores) {
            sum += s;
        }
        double avg = (double) sum / scores.length;
        System.out.println("总分: " + sum + ", 平均分: " + String.format("%.2f", avg));

        System.out.println();
    }

    static void twoDimensionalArray() {
        System.out.println("--- 3. 二维数组 ---");

        // 声明与初始化
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        System.out.println("行数: " + matrix.length);
        System.out.println("列数(第1行): " + matrix[0].length);

        // 遍历二维数组
        System.out.println("矩阵:");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        // 不规则数组（每行列数不同）
        int[][] jagged = {
            {1},
            {2, 3},
            {4, 5, 6}
        };
        System.out.println("不规则数组:");
        for (int[] row : jagged) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    static void arraysUtil() {
        System.out.println("--- 4. Arrays 工具类 ---");

        int[] arr = {5, 2, 8, 1, 9, 3};

        // toString：打印数组
        System.out.println("原数组: " + Arrays.toString(arr));

        // sort：排序（原地排序）
        Arrays.sort(arr);
        System.out.println("排序后: " + Arrays.toString(arr));

        // binarySearch：二分查找（必须有序）
        int index = Arrays.binarySearch(arr, 5);
        System.out.println("查找5的位置: index=" + index);

        // fill：填充
        int[] filled = new int[5];
        Arrays.fill(filled, 42);
        System.out.println("填充: " + Arrays.toString(filled));

        // copyOf：复制数组
        int[] copy = Arrays.copyOf(arr, arr.length);
        System.out.println("复制: " + Arrays.toString(copy));

        // copyOfRange：部分复制
        int[] partial = Arrays.copyOfRange(arr, 1, 4);  // [1, 4)
        System.out.println("部分复制(arr[1]~arr[3]): " + Arrays.toString(partial));

        // equals：比较数组
        System.out.println("arr == copy: " + Arrays.equals(arr, copy));

        System.out.println();
    }

    static void commonAlgorithms() {
        System.out.println("--- 5. 常见算法 ---");

        int[] arr = {38, 27, 43, 3, 9, 82, 10};

        // 找最大值/最小值
        int max = arr[0], min = arr[0];
        for (int v : arr) {
            if (v > max) max = v;
            if (v < min) min = v;
        }
        System.out.println("数组: " + Arrays.toString(arr));
        System.out.println("最大值: " + max + ", 最小值: " + min);

        // 反转数组
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        System.out.println("反转后: " + Arrays.toString(arr));

        // 冒泡排序演示
        int[] bubbleArr = {5, 1, 4, 2, 8};
        System.out.println("冒泡排序前: " + Arrays.toString(bubbleArr));
        for (int i = 0; i < bubbleArr.length - 1; i++) {
            for (int j = 0; j < bubbleArr.length - 1 - i; j++) {
                if (bubbleArr[j] > bubbleArr[j + 1]) {
                    int temp = bubbleArr[j];
                    bubbleArr[j] = bubbleArr[j + 1];
                    bubbleArr[j + 1] = temp;
                }
            }
        }
        System.out.println("冒泡排序后: " + Arrays.toString(bubbleArr));
        System.out.println();
    }
}
