/*
 Navicat Premium Dump SQL

 Source Server         : MyDB
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : watch_mall

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 03/01/2026 16:10:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `categoryName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `categoryImgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '机械表', '/api/uploads/avatar/5027395c-317c-4f1b-a99d-107932b3fa1f.jpg', '2026-01-02 13:34:35', '2026-01-03 15:32:48', 0);
INSERT INTO `category` VALUES (2, '电子表', '/api/uploads/avatar/5027395c-317c-4f1b-a99d-107932b3fa1f.jpg', '2026-01-03 15:33:03', '2026-01-03 15:33:03', 0);
INSERT INTO `category` VALUES (3, '石英表', '/api/uploads/avatar/5027395c-317c-4f1b-a99d-107932b3fa1f.jpg', '2026-01-03 15:33:13', '2026-01-03 15:33:13', 0);
INSERT INTO `category` VALUES (4, '配件', '/api/uploads/avatar/5027395c-317c-4f1b-a99d-107932b3fa1f.jpg', '2026-01-03 15:33:21', '2026-01-03 15:33:21', 0);
INSERT INTO `category` VALUES (5, '男士系列', '/api/uploads/avatar/5027395c-317c-4f1b-a99d-107932b3fa1f.jpg', '2026-01-03 15:33:28', '2026-01-03 15:33:28', 0);
INSERT INTO `category` VALUES (6, '女士系列', '/api/uploads/avatar/5027395c-317c-4f1b-a99d-107932b3fa1f.jpg', '2026-01-03 15:33:36', '2026-01-03 15:33:36', 0);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `productName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `imageUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT 0.00,
  `stock` int NULL DEFAULT 0,
  `categoryId` bigint NOT NULL,
  `isBanner` tinyint NULL DEFAULT 0,
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `categoryId`(`categoryId` ASC) USING BTREE,
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`categoryId`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `userAccount` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `userPassword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `avatarUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `gender` tinyint NULL DEFAULT 0,
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `balance` decimal(10, 2) NULL DEFAULT 0.00,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NULL DEFAULT 0,
  `userRole` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `userAccount`(`userAccount` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'Truth', 'Truth', NULL, '3666ea077cd253d01974fdb3040e49d3', '/api/uploads/avatar/5027395c-317c-4f1b-a99d-107932b3fa1f.jpg', 0, NULL, 100000.00, '2025-12-26 13:08:23', '2026-01-02 19:06:49', 0, 1);

SET FOREIGN_KEY_CHECKS = 1;
