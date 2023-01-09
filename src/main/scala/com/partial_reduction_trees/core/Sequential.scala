package com.partial_reduction_trees.core

object Sequential:
  //Scan in imperative and mutable way
  def scan(input: Array[Float], output: Array[Float]): Unit =
    require(input.length <= output.length, "Incorrect params")
    output(0) = 0
    var j = 1
    var max = 0f
    while (j < input.length) {
      val value = input(j) / j
      if value > max then max = value else ()
      output(j) = max
      j += 1
    }

