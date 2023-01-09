package com.partial_reduction_trees.core

import zio.*

object Parallel:
  private object Invariants:
    def requireUpsweep(input: Array[Float], from: Int, until: Int): Unit =
      require(from < until && from >= 0 && from < input.length & until <= input.length, "Incorrect params")

    def requireDownsweep(input: Array[Float], output: Array[Float], from: Int, until: Int): Unit =
      require(from < until && from >= 0 && from < input.length && until <= input.length
        && output.length >= until, s"Incorrect params")
  end Invariants

  private enum Tree(val maxPrevious: Float):
    case Node(left: Tree, right: Tree) extends Tree(left.maxPrevious.max(right.maxPrevious))
    case Leaf(from: Int, until: Int, override val maxPrevious: Float) extends Tree(maxPrevious)

  private def elemIndexValue(input: Array[Float], index: Int) = if (index == 0) 0f else input(index) / index

  import Invariants._
  private def upsweep(input: Array[Float], from: Int, until: Int, threshold: Int): UIO[Tree] =
    requireUpsweep(input, from, until)
    require(threshold > 0, "Incorrect param")
    def sequentialUpsweep(from: Int, until: Int) = ZIO.succeed {
      val maxPrevious = (from until until).foldLeft(0f) { (acc, i) => elemIndexValue(input, i).max(acc) }
      Tree.Leaf(from, until, maxPrevious)
    }
    def recUpsweep(from: Int, until: Int): UIO[Tree] = ZIO.suspendSucceed {
      val length = until - from
      if (length <= threshold)
        sequentialUpsweep(from, until)
      else
        val mid = from + length / 2
        recUpsweep(from, mid).zipWithPar(recUpsweep(mid, until)) { case (left, right) => Tree.Node(left, right) }
    }
    recUpsweep(from, until)

  private def downsweep(input: Array[Float], output: Array[Float], startingValue: Float, tree: Tree): UIO[Unit] =
    def recDownsweep(startingValue: Float, tree: Tree): UIO[Unit] =
      def sequentialDownsweep(input: Array[Float], output: Array[Float], startingValue: Float, from: Int, until: Int) =
        ZIO.succeed {
          (from until until).foreach { i =>
            val compValue = if (i == from) startingValue else output(i - 1)
            output(i) = elemIndexValue(input, i).max(compValue)
          }
        }
      ZIO.suspendSucceed {
        tree match {
          case Tree.Leaf(from, until, _) =>
            sequentialDownsweep(input, output, startingValue, from, until)
          case Tree.Node(left, right) =>
            recDownsweep(startingValue, left) <&> recDownsweep(left.maxPrevious max startingValue, right)
        }
      }
    recDownsweep(startingValue, tree)

  def scan(input: Array[Float], output: Array[Float], threshold: Int): UIO[Unit] =
    for {
      tree <- upsweep(input, 0, input.length, threshold)
      _ <- downsweep(input, output, Float.MinValue, tree)
    } yield ()

