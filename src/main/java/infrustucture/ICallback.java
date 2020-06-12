package infrustucture;

public interface ICallback {
    <T> void receivedData(int code,boolean isSuccess,T data);
}
