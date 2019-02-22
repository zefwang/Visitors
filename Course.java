interface ILo<T> {}
class MtLo<T> implements ILo<T> {}
class ConsLo<T> implements ILo<T> {
  T first;
  ILo<T> rest;
  
  ConsLo(T first, ILo<T> rest) {
    this.first = first;
    this.rest = rest;
  }
}

class Course {
  String name;
  ILo<Course> prereqs;
}

interface IFunc<A, R> {
  R apply(R arg);
}