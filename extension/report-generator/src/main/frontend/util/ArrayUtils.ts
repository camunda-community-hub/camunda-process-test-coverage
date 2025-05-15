const distinct = <T>(accessor: (item: T) => unknown = item => item) => (
    (value: T, index: number, array: T[]): boolean => (
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        array.indexOf(array.find(item => accessor(item) === accessor(value))!) === index
    )
);

export default distinct;
