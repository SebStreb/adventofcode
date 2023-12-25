import java.io.File

data class Wire(val from: Component, val to: Component)
data class Component(val name: String) : Comparable<Component> {
    override fun toString() = name
    override fun compareTo(other: Component) = name.compareTo(other.name)

    companion object {
        val components = mutableMapOf<String, Component>()
        val wires = mutableSetOf<Wire>()

        private fun parseOne(line: String) {
            val (name, connections) = line.split(": ")
            val component = components.getOrPut(name) { Component(name) }
            connections.split(" ").forEach { n ->
                val connection = components.getOrPut(n) { Component(n) }
                if (component < connection) wires.add(Wire(component, connection))
                else wires.add(Wire(connection, component))
            }
        }

        fun parse(lines: List<String>) {
            lines.map { parseOne(it) }
        }
    }
}

class Graph(private val components: Set<Component>, private val wires: Set<Wire>) {
    private val parents = mutableMapOf<Component, Component>()
    private val ranks = mutableMapOf<Component, Int>()

    init {
        components.forEach { c ->
            parents[c] = c
            ranks[c] = 0
        }
    }

    private fun find(component: Component): Component {
        val parent = parents[component]!!
        if (parent == component) return component
        val root = find(parent)
        parents[component] = root
        return root
    }

    private fun union(c1: Component, c2: Component) {
        val root1 = find(c1)
        val root2 = find(c2)
        if (root1 == root2) return
        if (ranks[root1]!! > ranks[root2]!!) parents[root2] = root1
        else {
            parents[root1] = root2
            if (ranks[root1] == ranks[root2]) ranks[root2] = ranks[root2]!! + 1
        }
    }

    fun kargerMinCut(): Int {
        val openWires = wires.toMutableList()

        var groups = components.size
        while (groups > 2) {
            val wire = openWires.random()
            openWires.remove(wire)

            val group1 = find(wire.from)
            val group2 = find(wire.to)

            if (group1 != group2) {
                groups--
                union(group1, group2)
            }
        }

        var minCut = 0
        for (wire in openWires) {
            val group1 = find(wire.from)
            val group2 = find(wire.to)
            if (group1 != group2) minCut++
        }
        return minCut
    }

    fun connectedComponents(): Set<Set<Component>> {
        val componentsByRoot = mutableMapOf<Component, MutableSet<Component>>()
        for (component in components) {
            val root = find(component)
            componentsByRoot.getOrPut(root) { mutableSetOf() }.add(component)
        }
        return componentsByRoot.values.toSet()
    }
}

fun main() {
    val file = File("inputs/c25")
    val lines = file.readLines()

    Component.parse(lines)
    val components = Component.components.values.toSet()
    val wires = Component.wires

    var graph: Graph
    do {
        graph = Graph(components, wires)
        val res = graph.kargerMinCut()
    } while (res != 3)

    val groups = graph.connectedComponents()
    val (g1, g2) = groups.toList()
    println(g1.size * g2.size)
}