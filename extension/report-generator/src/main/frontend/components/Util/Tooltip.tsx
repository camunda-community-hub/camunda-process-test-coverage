import { h, FunctionalComponent, RefObject, ComponentChildren } from 'preact';
import { useState, useRef, useEffect } from 'preact/hooks';
import {
    computePosition,
    offset,
    flip,
    shift,
    arrow,
    Placement,
    Middleware,
} from '@floating-ui/dom';

interface Props {
    title: string;
    placement?: Placement;
    children: ComponentChildren;
}

const Tooltip: FunctionalComponent<Props> = ({ title, placement = 'top', children }) => {
    const [visible, setVisible] = useState(false);
    const [styles, setStyles] = useState<{ top?: number; left?: number }>({});
    const [arrowStyles, setArrowStyles] = useState<{ left?: number; top?: number }>({});

    const referenceRef = useRef<HTMLElement>(null);
    const tooltipRef = useRef<HTMLDivElement>(null);
    const arrowRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!visible || !referenceRef.current || !tooltipRef.current) return;

        const middleware: Middleware[] = [
            offset(6),
            flip(),
            shift({ padding: 5 }),
            arrow({ element: arrowRef.current! }),
        ];

        computePosition(referenceRef.current, tooltipRef.current, {
            placement,
            middleware,
        }).then(({ x, y, placement, middlewareData }) => {
            setStyles({
                left: x,
                top: y,
            });

            const { arrow: arrowData } = middlewareData;
            if (arrowData) {
                setArrowStyles({
                    left: arrowData.x,
                    top: arrowData.y,
                });
            }
        });
    }, [visible, placement]);

    return (
        <>
      <span
          ref={referenceRef as RefObject<HTMLSpanElement>}
          onMouseEnter={() => setVisible(true)}
          onMouseLeave={() => setVisible(false)}
          class="inline-block"
      >
        {children}
      </span>

            {visible && (
                <div
                    ref={tooltipRef}
                    class="z-50 px-2 py-1 text-sm text-white bg-black bg-opacity-90 rounded select-none pointer-events-none fixed"
                    style={{ top: styles.top ?? 0, left: styles.left ?? 0, position: 'fixed' }}
                    role="tooltip"
                >
                    {title}
                    <div
                        ref={arrowRef}
                        class="absolute w-2 h-2 bg-black bg-opacity-90 rotate-45"
                        style={{
                            left: arrowStyles.left ?? '50%',
                            top: arrowStyles.top ?? '50%',
                            // Offset the arrow half its size to center it
                            transformOrigin: 'center',
                        }}
                    />
                </div>
            )}
        </>
    );
};

export default Tooltip;
