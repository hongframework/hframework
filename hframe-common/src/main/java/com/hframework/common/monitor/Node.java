package com.hframework.common.monitor;

import java.util.ArrayList;
import java.util.List;

public class Node<T>{
    private String keyValue;
    private T object;
    private List<Node> inputs;
    private List<Node> outputs;
    private Class objectClass;

    public Node(String keyValue, T object) {
        this.keyValue = keyValue;
        this.object = object;
        if(this.object != null) {
            this.objectClass = this.object.getClass();
        }

    }

    public void addOutput(Node node) {
        if(outputs == null) {
            outputs = new ArrayList<Node>();
        }
        outputs.add(node);
    }
    public void addInput(Node node) {
        if(inputs == null) {
            inputs = new ArrayList<Node>();
        }
        inputs.add(node);
    }

    public void setObject(T object) {
        this.object = object;
        if(this.object != null) {
            this.objectClass = this.object.getClass();
        }
    }

    public List<Node> getInputs() {
        return inputs;
    }

    public List<Node> getOutputs() {
        return outputs;
    }

    public <N> Node<N> getInput(Class<N> objectClass) {
        List<Node<N>> result = getInputs(objectClass);
        if(result.size() > 1) throw new RuntimeException(objectClass.getSimpleName() + " is not only !");
        if(result.size() == 0) return null;
        return result.get(0);
    }

    public <N> Node<N> getOutput(Class<N> objectClass) {
        List<Node<N>> result = getOutputs(objectClass);
        if(result.size() > 1) throw new RuntimeException(objectClass.getSimpleName() + " is not only !");
        if(result.size() == 0) return null;
        return result.get(0);
    }

    public <N> List<Node<N>> getInputs(Class<N> objectClass) {
        List<Node<N>> result = new ArrayList<Node<N>>();
        if(inputs != null) {
            for (Node input : inputs) {
                if(objectClass.equals(input.getObjectClass())) {
                    result.add(input);
                }
            }
        }

        return result;
    }

    public <N> List<Node<N>> getOutputs(Class<N> objectClass) {
        List<Node<N>> result = new ArrayList<Node<N>>();
        if(outputs != null) {
            for (Node input : outputs) {
                if(objectClass.equals(input.getObjectClass())) {
                    result.add(input);
                }
            }
        }

        return result;
    }

    public void removeOutput(Node node) {
        if(outputs != null) {
            outputs.remove(node);
        }
    }

    public void removeInput(Node node) {
        if(inputs != null) {
            inputs.remove(node);
        }
    }

    public String getKeyValue() {
        return keyValue;
    }

    public T getObject() {
        return object;
    }

    public Class getObjectClass() {
        return objectClass;
    }
}