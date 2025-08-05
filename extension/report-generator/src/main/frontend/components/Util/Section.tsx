import { h, FunctionalComponent, ComponentChildren } from 'preact';

interface Props {
    title: string;
    children?: ComponentChildren;
}

const Section: FunctionalComponent<Props> = ({ title, children }) => {
    return (
        <div class="border-2 border-gray-300 bg-white bg-opacity-50 rounded-md flex flex-col max-w-4xl w-full mx-auto mt-4">
            <span class="h-9 px-2.5 bg-gray-200 w-full block font-medium flex items-center">
                {title}
            </span>
            <div class="flex flex-col p-4">
                {children}
            </div>
        </div>
    );
};

export default Section;
