import { h } from 'preact';
import ViewerContainer from './Viewer/ViewerContainer';

const App = () => {
    return (
        <div class="min-h-screen flex flex-col">
            <ViewerContainer />
        </div>
    );
};

export default App;