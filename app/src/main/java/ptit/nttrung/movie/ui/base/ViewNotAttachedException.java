package ptit.nttrung.movie.ui.base;


class ViewNotAttachedException extends RuntimeException {
    ViewNotAttachedException() {
        super("Please call attachView() before proceeding!");
    }
}