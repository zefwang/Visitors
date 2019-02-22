interface IList<T> {
}

class MtList<T> implements IList<T> {
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }
}

class Course {
  String name;
  IList<Course> prereqs;

  Course(String name, IList<Course> prereqs) {
    this.name = name;
    this.prereqs = prereqs;
  }
}

interface IFunc<A, R> {
  R apply(A arg);
}

interface IListVisitor<T, R> extends IFunc<Course, R>{
  R apply(Course arg);
}
