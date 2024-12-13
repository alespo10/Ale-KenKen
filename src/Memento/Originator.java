package Memento;

public interface Originator
{
    Memento salva();
    void ripristina(Memento memento);
}
